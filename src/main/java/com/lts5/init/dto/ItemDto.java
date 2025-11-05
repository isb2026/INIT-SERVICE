package com.lts5.init.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;
    private Short tenantId;
    private Boolean isDelete;
    private Boolean isUse;
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
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private List<ItemProgressDto> itemProgress;
    private List<ProgressRouteDto> progressRoutes;
    private List<FileLinkDto> fileUrls;
}
