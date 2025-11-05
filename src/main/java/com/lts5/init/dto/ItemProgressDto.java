package com.lts5.init.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemProgressDto {

    private Long id;
    private Short tenantId;
    private Boolean isDelete;
    private Boolean isUse;
    private Long itemId;
    private ItemDto item;
    private Byte progressOrder;
    private String progressName;
    private Boolean isOutsourcing;
    private String progressTypeCode;
    private String progressTypeName;
    private Float unitWeight;
    private String unitTypeName;
    private String unitTypeCode;
    private Float defaultCycleTime;
    private Float optimalProgressInventoryQty;
    private Float safetyProgressInventoryQty;
    private String progressDefaultSpec;
    private String keyManagementContents;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
