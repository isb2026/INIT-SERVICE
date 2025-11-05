package com.lts5.init.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessTreeNodeDto {
    
    // TreeView UI에 필요한 기본 필드들
    private String id; // 고유 식별자 (progressId 기반)
    private String label; // 표시될 텍스트
    private String icon; // 아이콘 타입 (예: "process", "material", "item")
    private List<ProcessTreeNodeDto> children; // 하위 노드들
    private Boolean disabled; // 비활성화 여부
    
    // 공정 정보
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
    
    // 투입품 정보 (자재 노드일 때 사용)
    private Long mbomId;
    private Long rootItemId;
    private Long parentItemId;
    private Float inputNum;
    private String inputUnit;
    private String inputUnitCode;
    
    // 노드 타입 구분
    private String nodeType; // "PROCESS", "MATERIAL", "ITEM"
    
    // 추가 정보
    private Integer level; // 트리 깊이
    private String path; // 노드 경로
    private Boolean hasChildren; // 하위 노드 존재 여부
    private Integer childrenCount; // 하위 노드 개수
    
    // 메타 정보
    private Short tenantId;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
