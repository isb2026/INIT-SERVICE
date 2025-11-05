package com.lts5.init.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MbomTreeDto extends MbomDto {
    
    // 공정 순서 정보
    private Byte progressOrder;
    private Byte progressSequence;
    private String progressName;
    private String progressTypeName;
    
    // 트리 구조 정보
    private List<MbomTreeDto> children;
    private Integer level; // 트리 깊이 (0부터 시작)
    private String path; // 트리 경로 (예: "1.1.2")
    
    // 공정별 투입 정보
    private Float totalInputNum; // 해당 공정에서의 총 투입량
    private String inputUnitDisplay; // 단위 표시용
    
    // 계산된 정보
    private Boolean hasChildren; // 하위 BOM 존재 여부
    private Integer childrenCount; // 하위 BOM 개수
}
