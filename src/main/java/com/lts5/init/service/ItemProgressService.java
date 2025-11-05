package com.lts5.init.service;

import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.EntityNotFoundException;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.ItemProgressDto;
import com.lts5.init.entity.ItemProgress;
import com.lts5.init.payload.request.itemprogress.ItemProgressSearchRequest;
import com.lts5.init.repository.item.ItemRepository;
import com.lts5.init.repository.itemprogress.ItemProgressRepository;
import com.lts5.init.repository.code.CodeRepository;
import com.lts5.init.payload.request.progressvendor.ProgressVendorCreateRequest;
import com.primes.library.util.DynamicFieldQueryUtil;
import com.primes.library.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemProgressService extends BaseService {
    private final ItemProgressRepository itemProgressRepository;
    private final DynamicFieldQueryUtil dynamicFieldQueryUtil;
    private final ItemRepository itemRepository;
    private final ProgressVendorService progressVendorService;
    private final GlobalMapper globalMapper;
    private final CodeRepository codeRepository;
    @Transactional
    public ItemProgressDto create(ItemProgressDto dto) {
        // 공정순서가 null이면 자동 할당
        if (dto.getProgressOrder() == null) {
            byte nextOrder = (byte) (itemProgressRepository.findMaxProgressOrder(dto.getItemId()) + 1);
            dto.setProgressOrder(nextOrder);
        }

        // progressName이 비어있으면 progressTypeCode로 progressTypeName을 조회하여 설정
        if (dto.getProgressName() == null || dto.getProgressName().trim().isEmpty()) {
            if (dto.getProgressTypeCode() != null) {
                codeRepository.findByCodeValueAndIsDeleteFalse(dto.getProgressTypeCode())
                        .ifPresent(code -> dto.setProgressName(code.getCodeName()));
            }
        }

        ItemProgress entity = globalMapper.map(dto, ItemProgress.class);

        ItemProgress savedEntity = itemProgressRepository.save(entity);
        return enrichWithCodeNames(savedEntity);
    }
    
    @Transactional
    public List<ItemProgressDto> createList(List<ItemProgressDto> dtos) {
        // 공정순서 자동 할당 처리 및 progressName 자동 설정
        for (ItemProgressDto dto : dtos) {
            if (dto.getProgressOrder() == null) {
                byte nextOrder = (byte) (itemProgressRepository.findMaxProgressOrder(dto.getItemId()) + 1);
                dto.setProgressOrder(nextOrder);
            }
            
            // progressName이 비어있으면 progressTypeCode로 progressTypeName을 조회하여 설정
            if (dto.getProgressName() == null || dto.getProgressName().trim().isEmpty()) {
                if (dto.getProgressTypeCode() != null) {
                    codeRepository.findByCodeValueAndIsDeleteFalse(dto.getProgressTypeCode())
                            .ifPresent(code -> dto.setProgressName(code.getCodeName()));
                }
            }
        }

        List<ItemProgress> entities = dtos.stream()
                .map(dto -> globalMapper.map(dto, ItemProgress.class))
                .toList();
        
        List<ItemProgress> savedEntities = itemProgressRepository.saveAll(entities);
        List<ItemProgressDto> result = savedEntities.stream()
                .map(this::enrichWithCodeNames)
                .toList();
        
        // isOutsourcing이 true이고 vendorId와 unitCost가 있는 경우 ProgressVendor 생성
        List<ProgressVendorCreateRequest> progressVendorRequests = new ArrayList<>();
        
        for (int i = 0; i < dtos.size(); i++) {
            ItemProgressDto originalDto = dtos.get(i);
            ItemProgressDto createdDto = result.get(i);
            
            if (Boolean.TRUE.equals(originalDto.getIsOutsourcing())) {
                ProgressVendorCreateRequest progressVendorRequest = ProgressVendorCreateRequest.builder()
                    .progressId(createdDto.getId())
                    .isDefaultVendor(true)  // 기본 업체로 설정
                    .createBy(createdDto.getCreatedBy())
                    .build();
                
                progressVendorRequests.add(progressVendorRequest);
            }
        }
        
        // ProgressVendor 생성 (있는 경우에만)
        if (!progressVendorRequests.isEmpty()) {
            progressVendorService.createProgressVendors(progressVendorRequests);
        }
        
        return result;
    }
    
    @Transactional
    public ItemProgressDto update(Long id, ItemProgressDto dto) {
        dto.setId(id);  // DTO에 ID 설정
        return updateSingle(dto);
    }
    
    @Transactional
    public List<ItemProgressDto> updateAll(List<ItemProgressDto> dtos) {
        return dtos.stream()
                .map(this::updateSingle)
                .toList();
    }

    @Transactional
    public void delete(List<Long> ids) {
        List<ItemProgress> existingEntities = itemProgressRepository.findAllById(ids);
        
        if (existingEntities.size() != ids.size()) {
            List<Long> existingIds = existingEntities.stream().map(ItemProgress::getId).toList();
            List<Long> notFoundIds = new ArrayList<>(ids);
            notFoundIds.removeAll(existingIds);

            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR,
                String.format("Id가 %s인 ItemProgress 데이터가 없습니다.", notFoundIds));
        }

        // 연결된 ProgressVendor 먼저 삭제
        for (Long progressId : ids) {
            progressVendorService.deleteByProgressId(progressId);
        }

        // ItemProgress 삭제 (soft delete)
        for (ItemProgress entity : existingEntities) {
            entity.setDelete();
        }
        itemProgressRepository.saveAll(existingEntities);
    }

    public List<?> getFieldValues(String fieldName, ItemProgressSearchRequest searchRequest) {
        if (searchRequest == null) {
            return dynamicFieldQueryUtil.getFieldValues(ItemProgress.class, fieldName);
        }else{
            return dynamicFieldQueryUtil.getFieldValuesWithFilter(ItemProgress.class,  fieldName, searchRequest);
        }
    }

    public Page<ItemProgressDto> search(ItemProgressSearchRequest searchRequest, Pageable pageable) {
        return itemProgressRepository.search(searchRequest, pageable)
                .map(this::enrichWithCodeNames);
    }

    // ==================== 유틸리티 메서드 ====================

    private ItemProgressDto updateSingle(ItemProgressDto dto) {
        ItemProgress entity = itemProgressRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, String.format("Id 가 %d인 ItemProgress 데이터가 없습니다.", dto.getId())));
        
        // 공정순서 변경 처리
        if (dto.getProgressOrder() != null && !dto.getProgressOrder().equals(entity.getProgressOrder())) {
            handleProgressOrderChange(entity, dto.getProgressOrder());
        }
        
        // itemId가 있으면 Item 엔티티 조회해서 설정
        if (dto.getItemId() != null) {
            var item = itemRepository.findById(dto.getItemId()).orElse(null);
            try {
                var itemField = entity.getClass().getDeclaredField("item");
                itemField.setAccessible(true);
                itemField.set(entity, item);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // 필드 설정 실패 시 무시
            }
        }

        updateEntityFromDto(entity, dto);
        return enrichWithCodeNames(entity);
    }

    /**
     * 공정순서 변경 시 다른 공정들의 순서를 자동으로 조정
     */
    private void handleProgressOrderChange(ItemProgress entity, Byte newOrder) {
        Byte oldOrder = entity.getProgressOrder();
        Long itemId = entity.getItemId();
        
        if (oldOrder == null || newOrder == null) return;
        
        if (oldOrder < newOrder) {
            // 순서가 뒤로 이동: oldOrder+1 ~ newOrder 사이의 공정들을 1씩 앞으로 이동
            itemProgressRepository.decrementProgressOrders(itemId, oldOrder, newOrder);
        } else if (oldOrder > newOrder) {
            // 순서가 앞으로 이동: newOrder ~ oldOrder-1 사이의 공정들을 1씩 뒤로 이동
            itemProgressRepository.incrementProgressOrders(itemId, newOrder, oldOrder);
        }
    }

    /**
     * 코드 정보로 DTO를 풍부하게 만드는 메서드
     */
    private ItemProgressDto enrichWithCodeNames(ItemProgress entity) {
        ItemProgressDto dto = globalMapper.map(entity, ItemProgressDto.class);

        // progressTypeCode로 progressTypeName 조회
        if (dto.getProgressTypeCode() != null) {
            codeRepository.findByCodeValueAndIsDeleteFalse(dto.getProgressTypeCode())
                    .ifPresent(code -> dto.setProgressTypeName(code.getCodeName()));
        }
        
        // unitTypeCode로 unitTypeName 조회
        if (dto.getUnitTypeCode() != null) {
            codeRepository.findByCodeValueAndIsDeleteFalse(dto.getUnitTypeCode())
                    .ifPresent(code -> dto.setUnitTypeName(code.getCodeName()));
        }
        
        // progressName이 비어있으면 progressTypeName으로 설정
        if (dto.getProgressName() == null || dto.getProgressName().trim().isEmpty()) {
            if (dto.getProgressTypeName() != null) {
                dto.setProgressName(dto.getProgressTypeName());
            }
        }
        
        return dto;
    }
}