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
public class RootItemTreeDto {
    
    private Long rootItemId; // 루트 아이템 ID
    private ProductInfoDto productInfo; // 제품 정보
    private List<ProcessNodeDto> processTree; // 공정별 투입품 트리
    private Integer totalProcessCount; // 총 공정 개수
    private Integer totalInputItemCount; // 총 투입품 개수
}