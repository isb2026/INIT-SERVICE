package com.lts5.init.dto;

import com.lts5.init.entity.enums.OwnerType;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileLinkDto {

    private Long id;
    private Short tenantId;
    private Boolean isDelete;
    private Boolean isUse;
    private String ownerTable;
    private OwnerType ownerType;
    private String ownerTypeDescription;
    private Long ownerId;
    private String url;
    private Short sortOrder;
    private Boolean isPrimary;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}