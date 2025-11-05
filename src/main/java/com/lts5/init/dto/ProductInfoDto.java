package com.lts5.init.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoDto {

    private Long id;
    private Short itemNo;
    private String itemNumber;
    private String itemName;
    private String itemSpec;
    private String itemModel;
    private String itemType1;
    private String itemType2;
    private String itemType3;
    private String itemUnit;
    private String lotSize;
    private Double optimalInventoryQty;
    private Double safetyInventoryQty;
    private Boolean isUse;
    private Boolean isDelete;
    private Short tenantId;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}

