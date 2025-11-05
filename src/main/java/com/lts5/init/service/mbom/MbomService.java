package com.lts5.init.service.mbom;

import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.EntityNotFoundException;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.MbomDto;
import com.lts5.init.dto.MbomListDto;
import com.lts5.init.dto.ProcessTreeNodeDto;
import com.lts5.init.dto.FullBomTreeDto;
import com.lts5.init.dto.ItemInfo;
import com.lts5.init.dto.ProgressInfo;
import com.lts5.init.payload.request.mbom.MbomSearchRequest;
import com.lts5.init.entity.Mbom;
import com.lts5.init.repository.mbom.MbomRepository;
import com.primes.library.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MbomService extends BaseService {
    private final MbomRepository mbomRepository;
    private final GlobalMapper globalMapper;
    private final MbomValidationService mbomValidationService;
    private final MbomTreeService mbomTreeService;
    private final MbomUtilService mbomUtilService;

    // ==================== CRUD 메서드 ====================
    
    @Transactional
    public MbomDto create(MbomDto dto) {
        // 루트 아이템 데이터 정합성 검증 및 수정
        mbomValidationService.validateAndFixRootItemData(dto);
        
        // 순환 참조 검증
        mbomValidationService.validateCircularReference(dto);
        
        // 단위 코드로 단위명 자동 설정
        if (dto.getInputUnitCode() != null && !dto.getInputUnitCode().trim().isEmpty()) {
            String unitName = mbomUtilService.getUnitNameByCode(dto.getInputUnitCode());
            dto.setInputUnit(unitName);
        }
        
        Mbom entity = globalMapper.map(dto, Mbom.class);
        Mbom savedEntity = mbomRepository.save(entity);
        return globalMapper.map(savedEntity, MbomDto.class);
    }
    
    @Transactional
    public List<MbomDto> createList(List<MbomDto> dtos) {
        // 각 MBOM에 대해 데이터 정합성 검증, 순환 참조 검증 및 단위명 자동 설정
        for (MbomDto dto : dtos) {
            mbomValidationService.validateAndFixRootItemData(dto);
            mbomValidationService.validateCircularReference(dto);
            
            // 단위 코드로 단위명 자동 설정
            if (dto.getInputUnitCode() != null && !dto.getInputUnitCode().trim().isEmpty()) {
                String unitName = mbomUtilService.getUnitNameByCode(dto.getInputUnitCode());
                dto.setInputUnit(unitName);
            }
        }
        
        List<Mbom> entities = dtos.stream()
                .map(dto -> globalMapper.map(dto, Mbom.class))
                .toList();
        
        List<Mbom> savedEntities = mbomRepository.saveAll(entities);
        return savedEntities.stream()
                .map(entity -> globalMapper.map(entity, MbomDto.class))
                .toList();
    }
    
    @Transactional
    public MbomDto update(Long id, MbomDto dto) {
        dto.setId(id);  // DTO에 ID 설정
        return updateSingle(dto);
    }
    
    @Transactional
    public List<MbomDto> updateAll(List<MbomDto> dtos) {
        // 각 MBOM에 대해 순환 참조 검증 및 단위명 자동 설정
        for (MbomDto dto : dtos) {
            mbomValidationService.validateCircularReference(dto);
            
            // 단위 코드로 단위명 자동 설정
            if (dto.getInputUnitCode() != null && !dto.getInputUnitCode().trim().isEmpty()) {
                String unitName = mbomUtilService.getUnitNameByCode(dto.getInputUnitCode());
                dto.setInputUnit(unitName);
            }
        }
        
        return dtos.stream()
                .map(this::updateSingle)
                .toList();
    }

    @Transactional
    public void delete(List<Long> ids) {
        List<Mbom> existingEntities = mbomRepository.findAllById(ids);
        
        if (existingEntities.size() != ids.size()) {
            List<Long> existingIds = existingEntities.stream().map(Mbom::getId).toList();
            List<Long> notFoundIds = new ArrayList<>(ids);
            notFoundIds.removeAll(existingIds);

            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR,
                String.format("Id가 %s인 Mbom 데이터가 없습니다.", notFoundIds));
        }

        for (Mbom entity : existingEntities) {
            entity.delete();
        }
        mbomRepository.saveAll(existingEntities);
    }

    private MbomDto updateSingle(MbomDto dto) {
        // 수정 시에도 순환 참조 검증
        mbomValidationService.validateCircularReference(dto);
        
        // 단위 코드로 단위명 자동 설정
        if (dto.getInputUnitCode() != null && !dto.getInputUnitCode().trim().isEmpty()) {
            String unitName = mbomUtilService.getUnitNameByCode(dto.getInputUnitCode());
            dto.setInputUnit(unitName);
        }
        
        Mbom entity = mbomRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, String.format("Id 가 %d인 Mbom 데이터가 없습니다.", dto.getId())));
        updateEntityFromDto(entity, dto);
        return globalMapper.map(entity, MbomDto.class);
    }

    // ==================== 트리 관련 메서드 (위임) ====================
    public List<ProcessTreeNodeDto> getProcessTreeForUI(Long itemId) {
        return mbomTreeService.getProcessTreeForUI(itemId);
    }
    
    public List<ProcessTreeNodeDto> getRecursiveProcessTreeForUI(Long rootItemId, Integer maxDepth) {
        return mbomTreeService.getRecursiveProcessTreeForUI(rootItemId, maxDepth);
    }
    
    public FullBomTreeDto getFullBomTree() {
        return mbomTreeService.getFullBomTree();
    }

    // ==================== 검증 메서드 (위임) ====================
    
    public boolean canAddRelation(Long rootItemId, Long parentItemId, Long childItemId) {
        return mbomValidationService.canAddRelation(rootItemId, parentItemId, childItemId);
    }

    // ==================== 리스트용 API 메서드 ====================
    
    public List<MbomListDto> getMbomListByRootItem(Long rootItemId, Integer maxDepth, MbomSearchRequest searchRequest) {
        // 완제품 자체도 포함 (depth 0)
        MbomListDto rootItem = buildRootItemForList(rootItemId);

        if (rootItem == null) {
            return new ArrayList<>();
        }

        List<MbomListDto> result = new ArrayList<>();
        result.add(rootItem);
        buildMbomListRecursively(rootItemId, 1, "1", maxDepth, result, searchRequest);        
        return result;
    }
    
    private MbomListDto buildRootItemForList(Long rootItemId) {
        // 완제품 정보 조회 (테넌트 필터링 적용)
        List<Mbom> rootMboms = mbomRepository.findByItemIdAndParentItemIdIsNull(rootItemId);
            
        MbomListDto rootDto = new MbomListDto();
        rootDto.setItemId(rootItemId);
        rootDto.setIsRoot(true);
        rootDto.setDepth(0);
        rootDto.setPath("0");
        rootDto.setSequence(1);
        
        if (!rootMboms.isEmpty()) {
            Mbom rootMbom = rootMboms.get(0);
            // GlobalMapper로 기본 필드들 매핑
            MbomListDto mappedDto = globalMapper.map(rootMbom, MbomListDto.class);
            rootDto.setId(mappedDto.getId());
            rootDto.setParentItemId(mappedDto.getParentItemId());
            rootDto.setItemId(mappedDto.getItemId());
            rootDto.setInputNum(mappedDto.getInputNum());
            rootDto.setInputUnit(mappedDto.getInputUnit());
            rootDto.setInputUnitCode(mappedDto.getInputUnitCode());
        }
        
        // 아이템 정보 조회 및 설정
        ItemInfo itemInfo = mbomUtilService.buildItemInfo(rootItemId);
        rootDto.setItem(itemInfo);
        
        // 하위 투입품 존재 여부 확인 (테넌트 필터링 적용)
        List<Mbom> children = mbomRepository.findByParentItemId(rootItemId);
        rootDto.setHasChildren(!children.isEmpty());
        rootDto.setChildrenCount(children.size());
        
        return rootDto;
    }
    
    private void buildMbomListRecursively(Long parentItemId, int currentDepth, String parentPath, 
                                         Integer maxDepth, List<MbomListDto> result, MbomSearchRequest searchRequest) {
        // 최대 깊이 제한 확인
        if (maxDepth != null && currentDepth > maxDepth) {
            return;
        }
        
        // 현재 부모의 모든 투입품 조회 (테넌트 필터링 적용, 공정 순서대로)
        List<Mbom> childMboms;
        if (searchRequest != null) {
            // 검색 조건이 있는 경우 Repository의 search 메서드 활용
            searchRequest.setParentItemId(parentItemId);
            // 페이징 없이 모든 결과 조회 (최대 1000개로 제한)
            Pageable pageable = PageRequest.of(0, 1000);
            Page<Mbom> searchResult = mbomRepository.search(searchRequest, pageable);
            childMboms = searchResult.getContent();
            System.out.println("childMboms: " + childMboms);
        } else {
            // 검색 조건이 없는 경우 기존 방식 사용
            childMboms = mbomRepository.findByParentItemIdOrderByParentProgressId(parentItemId);
        }
        
        for (int i = 0; i < childMboms.size(); i++) {
            Mbom childMbom = childMboms.get(i);
            String currentPath = parentPath + "." + (i + 1);
            
            // 투입품 DTO 생성
            MbomListDto childDto = globalMapper.map(childMbom, MbomListDto.class);
            childDto.setDepth(currentDepth);
            childDto.setPath(currentPath);
            childDto.setSequence(i + 1);
            
            // 아이템 정보 설정
            ItemInfo childItemInfo = mbomUtilService.buildItemInfo(childMbom.getItemId());
            childDto.setItem(childItemInfo);
            
            // 두 공정 정보 설정
            ProgressInfo parentProgress = mbomUtilService.buildParentProgressInfo(childMbom.getParentProgressId());
            ProgressInfo itemProgress = mbomUtilService.buildItemProgressInfo(childMbom.getItemProgressId());
            childDto.setParentProgress(parentProgress);
            childDto.setItemProgress(itemProgress);
            
            // 하위 투입품 존재 여부 확인 (테넌트 필터링 적용)
            List<Mbom> grandChildren = mbomRepository.findByParentItemId(childMbom.getItemId());
            childDto.setHasChildren(!grandChildren.isEmpty());
            childDto.setChildrenCount(grandChildren.size());
            
            result.add(childDto);
            
            // 재귀적으로 하위 투입품들 조회
            if (!grandChildren.isEmpty()) {
                buildMbomListRecursively(childMbom.getItemId(), currentDepth + 1, currentPath, maxDepth, result, searchRequest);
            }
        }
    }
}