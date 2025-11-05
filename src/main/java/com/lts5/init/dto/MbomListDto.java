package com.lts5.init.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MbomListDto {
    
    private Long id;
    private Short tenantId;
    private Boolean isDelete;
    
    // 부모-자식 관계 정보
    private Long parentItemId;
    private Long itemId;
    private Boolean isRoot;
    
    // 공정 정보
    private Long parentProgressId;
    private Long itemProgressId;
    
    // 투입 정보
    private Float inputNum;
    private String inputUnitCode;
    private String inputUnit;
    
    // 아이템 정보 객체
    private ItemInfo item;
    
    // 부모 공정 정보 객체 (부모 아이템 기준으로 어느 공정에서 투입되는지)
    private ProgressInfo parentProgress;
    
    // 아이템 공정 정보 객체 (해당 아이템 자체의 공정 정보)
    private ProgressInfo itemProgress;
    
    // 리스트 표시용 추가 정보
    private Integer depth;              // 깊이 (0: 완제품, 1: 1단계 투입품, 2: 2단계 투입품...)
    private String path;               // 경로 (예: "1.2.1")
    private Integer sequence;          // 같은 레벨에서의 순서
    private Boolean hasChildren;       // 하위 투입품 존재 여부
    private Integer childrenCount;     // 직계 하위 투입품 개수
    
    // 메타데이터
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
