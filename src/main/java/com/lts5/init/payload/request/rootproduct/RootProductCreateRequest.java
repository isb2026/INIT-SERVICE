package com.lts5.init.payload.request.rootproduct;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 루트 제품 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "루트 제품 생성 요청")
public class RootProductCreateRequest {

    @NotNull(message = "아이템 ID는 필수입니다.")
    @Schema(description = "아이템 ID", example = "151962761891840", required = true)
    private Long itemId;

    @NotBlank(message = "제품명은 필수입니다.")
    @Size(max = 100, message = "제품명은 100자 이내로 입력해주세요.")
    @Schema(description = "제품명", example = "스마트폰 A", required = true)
    private String productName;

    @Size(max = 50, message = "제품 코드는 50자 이내로 입력해주세요.")
    @Schema(description = "제품 코드", example = "PROD-001")
    private String productCode;

    @Size(max = 500, message = "제품 설명은 500자 이내로 입력해주세요.")
    @Schema(description = "제품 설명", example = "고성능 스마트폰")
    private String description;

    @Builder.Default
    @Schema(description = "활성 상태", example = "true")
    private Boolean isActive = true;
}
