package com.lts5.init.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeDto {

    private Long id;
    private Long codeGroupId;
    private CodeGroupDto codeGroup;
    private Short tenantId;
    private Boolean isDelete;
    private Boolean isUse;
    private String codeValue;
    private String codeName;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
