package com.lts5.init.dto;

import com.lts5.init.entity.RootProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 루트 제품 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RootProductDto {

    private Long id;
    private Integer tenantId;
    private Boolean isDelete;
    private Long itemId;
    private String productName; // 제품명
    private String productCode; // 제품 코드
    private String description; // 제품 설명
    private Boolean isActive; // 활성 상태
    private Integer totalProcessCount; // 총 공정 수
    private Integer totalInputItemCount; // 총 투입품 수
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
