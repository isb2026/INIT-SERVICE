package com.lts5.init.payload.request.mbom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Mbom 검색 요청")
public class MbomSearchRequest {

    @Schema(description = "생성일시 시작", example = "2024-01-01T00:00:00")
    private String createdAtStart;

    @Schema(description = "생성일시 종료", example = "2024-12-31T23:59:59")
    private String createdAtEnd;

    @Schema(description = "생성자", example = "admin")
    private String createdBy;

    @Schema(description = "수정일시 시작", example = "2024-01-01T00:00:00")
    private String updatedAtStart;

    @Schema(description = "수정일시 종료", example = "2024-12-31T23:59:59")
    private String updatedAtEnd;

    @Schema(description = "수정자", example = "admin")
    private String updatedBy;

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "부모 item_id", example = "1")
    private Long parentItemId;

    @Schema(description = "item_id", example = "1")
    private Long itemId;

    @Schema(description = "완제품 여부", example = "true")
    private Boolean isRoot;

    @Schema(description = "투입될 부모의 공정 ID", example = "1")
    private Long parentProgressId;

    @Schema(description = "아이템의 투입공정ID", example = "1")
    private Long itemProgressId;

    @Schema(description = "부품 투입량")
    private Float inputNum;

    @Schema(description = "투입단위", example = "CODE001")
    private String inputUnitCode;

    @Schema(description = "단위명", example = "샘플값")
    private String inputUnit;
}
