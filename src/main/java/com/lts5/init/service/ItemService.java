package com.lts5.init.service;

import com.lts5.init.dto.FileLinkDto;
import com.lts5.init.entity.FileLink;
import com.lts5.init.payload.request.filelink.FileLinkUpdateInfo;
import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.EntityNotFoundException;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.ItemDto;
import com.lts5.init.entity.Item;
import com.lts5.init.payload.request.item.ItemCreateRequest;
import com.lts5.init.payload.request.item.ItemUpdateRequest;
import com.lts5.init.payload.request.item.ItemUpdateAllRequest;
import com.lts5.init.payload.request.item.ItemSearchRequest;
import com.lts5.init.payload.response.item.ItemSearchResponse;
import com.lts5.init.repository.item.ItemRepository;
import com.lts5.init.repository.filelink.FileLinkRepository;
import com.primes.library.util.DynamicFieldQueryUtil;
import com.primes.library.service.BaseService;
import com.primes.library.util.NumberCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService extends BaseService {
    private final ItemRepository itemRepository;
    private final FileLinkRepository fileLinkRepository;
    private final FileLinkService fileLinkService;
    private final DynamicFieldQueryUtil dynamicFieldQueryUtil;
    private final NumberCodeGenerator numberCodeGenerator;
    private final GlobalMapper globalMapper;

    @Transactional
    public List<ItemDto> createList(List<ItemCreateRequest> requests) {
        List<ItemDto> results = new ArrayList<>();
        
        for (ItemCreateRequest request : requests) {
            // NumberCodeGenerator를 사용하여 itemNo 자동 생성
            NumberCodeGenerator.CodeGeneratorParam<Void> param =
                    NumberCodeGenerator.CodeGeneratorParam.series(
                            (Void v) -> itemRepository.countSeries(),
                            "default"
                    );
            Short nextItemNo = numberCodeGenerator.generateShort(param);

            ItemDto itemDto = globalMapper.map(request, ItemDto.class);
            itemDto.setItemNo(nextItemNo);

            Item entity = globalMapper.map(itemDto, Item.class);
            Item savedEntity = itemRepository.save(entity);

            if (request.getFileUrls() != null && !request.getFileUrls().isEmpty()) {
                fileLinkService.createList(savedEntity.getId(), request.getFileUrls());
            }

            // 저장된 엔티티를 다시 조회해서 FileLink 포함
            Item refreshedEntity = itemRepository.findById(savedEntity.getId()).orElse(savedEntity);
            results.add(globalMapper.map(refreshedEntity, ItemDto.class));
        }

        return results;
    }

    @Transactional
    public ItemDto update(Long id, ItemDto dto) {
        dto.setId(id);
        return updateSingle(dto);
    }

    @Transactional
    public ItemDto update(Long id, ItemUpdateRequest request) {
        ItemDto dto = globalMapper.map(request, ItemDto.class);
        dto.setId(id);

        updateSingle(dto);

        if (request.getFileUrls() != null && !request.getFileUrls().isEmpty()) {
            // 기존 파일과 새 파일을 분리해서 처리
            List<FileLinkUpdateInfo> existingFiles = request.getFileUrls().stream()
                    .filter(info -> info.getId() != null)
                    .toList();
            
            List<FileLinkUpdateInfo> newFiles = request.getFileUrls().stream()
                    .filter(info -> info.getId() == null)
                    .toList();
            
            // 기존 파일 업데이트
            if (!existingFiles.isEmpty()) {
                List<FileLinkDto> fileLinkDtos = existingFiles.stream()
                        .map(info -> {
                            FileLinkDto fileLinkDto = globalMapper.map(info, FileLinkDto.class);
                            fileLinkDto.setOwnerTable("items");
                            fileLinkDto.setOwnerId(id);
                            return fileLinkDto;
                        })
                        .toList();
                fileLinkService.updateAll(fileLinkDtos);
            }
            
            // 새 파일 생성
            if (!newFiles.isEmpty()) {
                fileLinkService.createList(id, newFiles);
            }
        }

        // FETCH JOIN으로 FileLink 포함해서 조회
        Item refreshedEntity = itemRepository.findByIdWithFileLinks(id).orElseThrow(() -> 
                new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, String.format("Id 가 %d인 Item 데이터가 없습니다.", id)));
        return globalMapper.map(refreshedEntity, ItemDto.class);
    }

    @Transactional
    public List<ItemDto> updateAll(List<ItemUpdateAllRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }

        List<ItemDto> results = new ArrayList<>();
        
        // ID 목록 추출하여 일괄 존재 여부 확인
        List<Long> itemIds = requests.stream()
                .map(ItemUpdateAllRequest::getId)
                .toList();
        
        Map<Long, Item> existingItems = itemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
                
        // 존재하지 않는 ID 확인
        List<Long> notFoundIds = itemIds.stream()
                .filter(id -> !existingItems.containsKey(id))
                .toList();
                
        if (!notFoundIds.isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("Id가 %s인 Item 데이터가 없습니다.", notFoundIds));
        }

        for (ItemUpdateAllRequest request : requests) {
            // 1. Item 업데이트
            ItemDto itemDto = globalMapper.map(request, ItemDto.class);
            itemDto.setId(request.getId());
            ItemDto updatedItem = updateSingle(itemDto);

            // 2. FileLink 처리 (있는 경우에만)
            if (request.getFileUrls() != null && !request.getFileUrls().isEmpty()) {
                processFileLinks(request);
            }

            results.add(updatedItem);
        }

        return results;
    }
    
    private void processFileLinks(ItemUpdateAllRequest request) {
        // 기존 파일과 새 파일을 분리
        List<FileLinkUpdateInfo> existingFiles = request.getFileUrls().stream()
                .filter(info -> info.getId() != null)
                .toList();
        
        List<FileLinkUpdateInfo> newFiles = request.getFileUrls().stream()
                .filter(info -> info.getId() == null)
                .toList();
        
        // 기존 파일 업데이트 (병렬 처리 가능)
        if (!existingFiles.isEmpty()) {
            List<FileLinkDto> fileLinkDtos = existingFiles.stream()
                    .map(info -> {
                        FileLinkDto fileLinkDto = globalMapper.map(info, FileLinkDto.class);
                        fileLinkDto.setOwnerTable("items");
                        fileLinkDto.setOwnerId(request.getId());
                        return fileLinkDto;
                    })
                    .toList();
            fileLinkService.updateAll(fileLinkDtos);
        }
        
        // 새 파일 생성
        if (!newFiles.isEmpty()) {
            fileLinkService.createList(request.getId(), newFiles);
        }
    }

    @Transactional
    public void delete(List<Long> ids) {
        List<Item> existingEntities = itemRepository.findAllById(ids);

        if (existingEntities.size() != ids.size()) {
            List<Long> existingIds = existingEntities.stream().map(Item::getId).toList();
            List<Long> notFoundIds = new ArrayList<>(ids);
            notFoundIds.removeAll(existingIds);

            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR,
                    String.format("Id가 %s인 Item 데이터가 없습니다.", notFoundIds));
        }

        // 연관된 FileLink ID
        List<Long> allFileLinkIds = new ArrayList<>();
        
        for (Item entity : existingEntities) {
            List<FileLink> fileLinks = fileLinkRepository.findByOwnerTableAndOwnerId("items", entity.getId());
            
            if (!fileLinks.isEmpty()) {
                List<Long> fileLinkIds = fileLinks.stream()
                    .map(FileLink::getId)
                    .toList();
                allFileLinkIds.addAll(fileLinkIds);
            }
        }
        
        if (!allFileLinkIds.isEmpty()) {
            fileLinkService.delete(allFileLinkIds);
        }
        
        for (Item entity : existingEntities) {
            entity.delete();
        }
        
        itemRepository.saveAll(existingEntities);
    }

    public List<?> getFieldValues(String fieldName, ItemSearchRequest searchRequest) {
        if (searchRequest == null) {
            return dynamicFieldQueryUtil.getFieldValues(Item.class, fieldName);
        }else{
            return dynamicFieldQueryUtil.getFieldValuesWithFilter(Item.class,  fieldName, searchRequest);
        }
    }

    public Page<ItemSearchResponse> search(ItemSearchRequest searchRequest, Pageable pageable) {
        // ItemSearchResponse DTO를 직접 반환
        return itemRepository.search(searchRequest, pageable);
    }

    public List<ItemSearchResponse> findAll(ItemSearchRequest searchRequest) {
        // 페이징 없이 전체 리스트 반환
        return itemRepository.searchAll(searchRequest);
    }

    // ==================== 유틸리티 메서드 ====================

    private ItemDto updateSingle(ItemDto dto) {
        Item entity = itemRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, String.format("Id 가 %d인 Item 데이터가 없습니다.", dto.getId())));
        updateEntityFromDto(entity, dto);
        return globalMapper.map(entity, ItemDto.class);
    }
}