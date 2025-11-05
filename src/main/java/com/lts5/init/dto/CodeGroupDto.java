package com.lts5.init.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeGroupDto {

    private Long id;
    private Short tenantId;
    private Boolean isDelete;
    private Boolean isUse;
    private Long parentId;
    private Boolean isRoot;
    private String groupCode;
    private String groupName;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private List<CodeDto> codes;
    private List<CodeGroupDto> children;
}
