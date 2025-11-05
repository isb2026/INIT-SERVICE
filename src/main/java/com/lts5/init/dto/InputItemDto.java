package com.lts5.init.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공정 투입품 정보 DTO")
public class InputItemDto {
    
    @Schema(description = "MBOM ID", example = "155230503243776")
    private Long mbomId;
    
    @Schema(description = "아이템 ID", example = "154039377530880")
    private Long itemId;
    
    @Schema(description = "아이템명", example = "아몬드 브리즈")
    private String itemName;
    
    @Schema(description = "투입량", example = "1.0")
    private Float inputNum;
    
    @Schema(description = "투입 단위 코드", example = "PRD-006-001")
    private String inputUnitCode;
    
    @Schema(description = "투입 단위명", example = "EA")
    private String inputUnit;
    
    @Schema(description = "경로", example = "1.1")
    private String path;
    
    @Schema(description = "제품 정보")
    private ProductInfoDto productInfo;
    
    @Schema(description = "생성일시", example = "2025-09-10 19:02:24")
    private String createdAt;
    
    @Schema(description = "생성자", example = "dev-user")
    private String createdBy;
}

