package com.lts5.init.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressRouteDto {

    private Long id;
    private Short tenantId;
    private Boolean isDelete;
    private Boolean isUse;
    private Long itemId;
    private ItemDto item;
    private Byte progressSequence;
    private String progressTypeCode;
    private String progressTypeName;
    private String progressRealName;
    private Double defaultCycleTime;
    private Double lotSize;
    private String lotUnit;
    private Double optimalProgressInventoryQty;
    private Double safetyProgressInventoryQty;
    private String progressDefaultSpec;
    private String keyManagementContents;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
