package com.lts5.init.service.mbom;

import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.EntityNotFoundException;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.ProductInfoDto;
import com.lts5.init.dto.InputItemDto;
import com.lts5.init.dto.ItemInfo;
import com.lts5.init.dto.ProgressInfo;
import com.lts5.init.entity.Mbom;
import com.lts5.init.entity.Item;
import com.lts5.init.entity.ItemProgress;
import com.lts5.init.entity.Code;
import com.lts5.init.repository.item.ItemRepository;
import com.lts5.init.repository.itemprogress.ItemProgressRepository;
import com.lts5.init.repository.code.CodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MbomUtilService {
    private final ItemRepository itemRepository;
    private final ItemProgressRepository itemProgressRepository;
    private final CodeRepository codeRepository;
    private final GlobalMapper globalMapper;

    /**
     * 제품 정보 DTO 생성 (전체 정보 포함)
     * @param itemId 아이템 ID
     * @return 제품 정보 DTO
     */
    public ProductInfoDto buildProductInfo(Long itemId) {
        return itemRepository.findById(itemId)
                .map(item -> ProductInfoDto.builder()
                        .id(item.getId())
                        .itemNo(item.getItemNo())
                        .itemNumber(item.getItemNumber())
                        .itemName(item.getItemName())
                        .itemSpec(item.getItemSpec())
                        .itemModel(item.getItemModel())
                        .itemType1(item.getItemType1())
                        .itemType2(item.getItemType2())
                        .itemType3(item.getItemType3())
                        .itemUnit(item.getItemUnit())
                        .lotSize(item.getLotSize())
                        .optimalInventoryQty(item.getOptimalInventoryQty())
                        .safetyInventoryQty(item.getSafetyInventoryQty())
                        .isUse(item.getIsUse())
                        .isDelete(item.getIsDelete())
                        .tenantId(item.getTenantId())
                        .createdAt(item.getCreatedAt())
                        .createdBy(item.getCreatedBy())
                        .updatedAt(item.getUpdatedAt())
                        .updatedBy(item.getUpdatedBy())
                        .build())
                .orElse(null);
    }

    /**
     * 투입품 DTO 생성
     * @param mbom MBOM 엔티티
     * @param path 트리 경로
     * @return 투입품 DTO
     */
    public InputItemDto buildInputItemDto(Mbom mbom, String path) {
        // 투입품의 제품 정보 조회
        ProductInfoDto productInfo = buildProductInfo(mbom.getItemId());
        
        InputItemDto dto = globalMapper.map(mbom, InputItemDto.class);
        dto.setPath(path);
        dto.setItemName(productInfo.getItemName()); // productInfo에서 가져옴
        dto.setProductInfo(productInfo); // 전체 제품 정보 포함
        dto.setCreatedAt(mbom.getCreatedAt() != null ? mbom.getCreatedAt().toString() : null);
        
        return dto;
    }

    /**
     * 단위 코드로 단위명 조회
     * @param unitCode 단위 코드 (예: "KG", "EA", "PRD-006-001" 등)
     * @return 단위명 (예: "킬로그램", "개", "제품명" 등), 없으면 코드 그대로 반환
     */
    public String getUnitNameByCode(String unitCode) {
        if (unitCode == null || unitCode.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 코드 테이블에서 직접 code_value로 조회
            Code codeEntity = codeRepository.findByCodeValueAndIsDeleteFalse(unitCode)
                    .orElse(null);
            
            if (codeEntity != null) {
                return codeEntity.getCodeName();
            } else {
                return unitCode;
            }
            
        } catch (Exception e) {
            log.error("코드 조회 중 오류 발생 - 코드: {}, 오류: {}", unitCode, e.getMessage());
            return unitCode; // 오류 시 코드 그대로 반환
        }
    }

    /**
     * 아이템 정보 객체를 생성하여 반환
     * @param itemId 아이템 ID
     * @return 아이템 정보 객체
     */
    public ItemInfo buildItemInfo(Long itemId) {
        Item itemEntity = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                        String.format("Id가 %d인 Item 데이터가 없습니다.", itemId)));
        return globalMapper.map(itemEntity, ItemInfo.class);
    }
    
    /**
     * 부모 공정 정보 객체를 생성하여 반환
     * @param parentProgressId 부모 공정 ID (부모 아이템 기준으로 어느 공정에서 투입되는지)
     * @return 부모 공정 정보 객체
     */
    public ProgressInfo buildParentProgressInfo(Long parentProgressId) {
        if (parentProgressId == null) {
            throw new com.primes.library.common.exceptions.IllegalArgumentException(ErrorCode.BAD_DATA_ERROR, "parentProgressId는 null일 수 없습니다.");
        }
        
        ItemProgress parentProgressEntity = itemProgressRepository.findById(parentProgressId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                        String.format("Id가 %d인 ItemProgress 데이터가 없습니다.", parentProgressId)));
        return globalMapper.map(parentProgressEntity, ProgressInfo.class);
    }
    
    /**
     * 아이템 공정 정보 객체를 생성하여 반환
     * @param itemProgressId 아이템 공정 ID (해당 아이템 자체의 공정 정보)
     * @return 아이템 공정 정보 객체
     */
    public ProgressInfo buildItemProgressInfo(Long itemProgressId) {
        if (itemProgressId == null) {
            throw new com.primes.library.common.exceptions.IllegalArgumentException(ErrorCode.BAD_DATA_ERROR, "itemProgressId는 null일 수 없습니다.");
        }
        
        ItemProgress itemProgressEntity = itemProgressRepository.findById(itemProgressId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                        String.format("Id가 %d인 ItemProgress 데이터가 없습니다.", itemProgressId)));
        return globalMapper.map(itemProgressEntity, ProgressInfo.class);
    }
}
