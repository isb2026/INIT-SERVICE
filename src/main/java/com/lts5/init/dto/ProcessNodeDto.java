package com.lts5.init.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessNodeDto {
    
    private Long progressId; // 공정 ID
    private Integer progressOrder; // 공정 순서
    private String progressName; // 공정명
    private String progressTypeName; // 공정 타입명
    private List<InputItemDto> inputItems; // 공정에 투입되는 아이템들
    private String path; // 트리 경로
    private Integer inputItemCount; // 투입품 개수
}