package com.lts5.init.payload.request.mbom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Mbom Update 요청")
public class MbomUpdateRequest {

    @Schema(description = "부모 item_id (완제품인 경우 null)", example = "1")
    private Long parentItemId;

    @Schema(description = "item_id", example = "1")
    private Long itemId;

    @Schema(description = "완제품 여부 (true: 완제품, false: 투입품)", example = "false")
    private Boolean isRoot;

    @Schema(description = "투입될 부모의 공정 ID", example = "1")
    private Long parentProgressId;

    @Schema(description = "부품 투입량")
    private Float inputNum;

    @Schema(description = "아이템의 투입공정ID", example = "1")
    private Long itemProgressId;

    @Size(max = 20, message = "투입단위은 20자 이내로 입력해주세요.")
    @Schema(description = "투입단위", example = "CODE001")
    private String inputUnitCode;

    @Size(max = 10, message = "단위명은 10자 이내로 입력해주세요.")
    @Schema(description = "단위명", example = "샘플값")
    private String inputUnit;
}
