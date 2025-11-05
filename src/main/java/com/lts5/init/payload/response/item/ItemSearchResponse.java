package com.lts5.init.payload.response.item;

import com.lts5.init.dto.FileLinkDto;
import com.lts5.init.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchResponse {
    
    // Item 엔티티의 기본 필드들
    private Long id;
    private Short itemNo;
    private String itemNumber;
    private String itemName;
    private String itemSpec;
    private String itemModel;
    private String itemUnit;
    private String lotSize;
    private Double optimalInventoryQty;
    private Double safetyInventoryQty;
    private Boolean isUse;
    private Boolean isDelete;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private Short tenantId;
    
    // code 필드들 추가 (codeValue와 codeName)
    private String itemType1Code;
    private String itemType1Value;
    private String itemType2Code;
    private String itemType2Value;
    private String itemType3Code;
    private String itemType3Value;
    private String lotSizeCode;
    private String lotSizeValue;
    
    // FileLink 정보
    private List<FileLinkDto> fileUrls;
}
