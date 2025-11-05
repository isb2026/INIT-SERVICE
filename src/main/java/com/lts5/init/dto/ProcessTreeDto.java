package com.lts5.init.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessTreeDto {
    
    // 공정 기본 정보
    private Long progressId;
    private String progressName;
    private Byte progressOrder;
    private String progressTypeName;
    private Boolean isOutsourcing;
    private Float defaultCycleTime;
    
    // 아이템 정보
    private Long itemId;
    private String itemName;
    private String itemNumber;
    
    // 투입품 정보 (해당 공정에 투입되는 MBOM들)
    private List<MbomDto> inputMaterials;
    private Integer inputMaterialsCount;
    private Boolean hasInputMaterials;
    
    // 트리 구조 정보
    private List<ProcessTreeDto> childProcesses; // 하위 공정들
    private Integer level; // 트리 깊이
    private String path; // 공정 경로 (예: "1.2.3")
    private Boolean hasChildren;
    private Integer childrenCount;
    
    // 메타 정보
    private Short tenantId;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
