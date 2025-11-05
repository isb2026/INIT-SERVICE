package com.lts5.init.payload.request.itemprogress;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ItemProgress 검색 요청")
public class ItemProgressSearchRequest {

    @Schema(description = "사용 여부", example = "true")
    private Boolean isUse;

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

    @Schema(description = "데이터 회계년", example = "1")
    private Short accountYear;

    @Schema(description = "item 테이블 참조", example = "1")
    private Long itemId;

    @Schema(description = "공정 순서", example = "1", type = "integer", format = "int32")
    private Byte progressOrder;

    @Schema(description = "공정 이름", example = "샘플명")
    private String progressName;

    @Schema(description = "외주 공정 여부 (0: 내공정, 1: 외주)", example = "true")
    private Boolean isOutsourcing;

    @Schema(description = "공정 타입 코드", example = "001")
    private String progressTypeCode;

    @Schema(description = "공정 타입 이름", example = "가공공정")
    private String progressTypeName;

    @Schema(description = "공정단중", example = "1.5")
    private Float unitWeight;

    @Schema(description = "공정단중의 단위", example = "kg")
    private String unitTypeName;

    @Schema(description = "공정단중 단위 Code값", example = "PRD-006-001")
    private String unitTypeCode;

    @Schema(description = "기본 사이클 타임", example = "10.5")
    private Float defaultCycleTime;

    @Schema(description = "적정 공정 재고 수량", example = "100.0")
    private Float optimalProgressInventoryQty;

    @Schema(description = "안전 공정 재고 수량", example = "50.0")
    private Float safetyProgressInventoryQty;

    @Schema(description = "공정 기본 사양", example = "표준 사양")
    private String progressDefaultSpec;

    @Schema(description = "주요 관리 내용", example = "품질 관리, 생산성 향상")
    private String keyManagementContents;
}
