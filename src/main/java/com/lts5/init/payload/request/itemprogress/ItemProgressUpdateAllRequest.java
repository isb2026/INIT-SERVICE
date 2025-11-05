package com.lts5.init.payload.request.itemprogress;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.Boolean;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ItemProgress 일괄 Update 요청")
public class ItemProgressUpdateAllRequest {

    @NotNull(message = "ID는 필수입니다.")
    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "사용여부", example = "true")
    private Boolean isUse;

    @Schema(description = "데이터 회계년", example = "1")
    private Short accountYear;

    @Schema(description = "item 테이블 참조", example = "1")
    private Long itemId;

    @Schema(description = "공정 순서", example = "1", type = "integer", format = "int32")
    private Byte progressOrder;

    @Size(max = 100, message = "공정 이름은 100자 이내로 입력해주세요.")
    @Schema(description = "공정 이름", example = "샘플명")
    private String progressName;

    @Schema(description = "외주 공정 여부 (0: 내공정, 1: 외주)", example = "true")
    private Boolean isOutsourcing;

    @Schema(description = "공정 타입 코드", example = "001", maxLength = 3)
    private String progressTypeCode;

    @Schema(description = "공정 타입 이름", example = "가공공정", maxLength = 100)
    private String progressTypeName;

    @Schema(description = "공정단중", example = "1.5")
    private Float unitWeight;

    @Schema(description = "공정단중의 단위", example = "kg", maxLength = 100)
    private String unitTypeName;

    @Schema(description = "공정단중 단위 Code값", example = "PRD-006-001", maxLength = 50)
    private String unitTypeCode;

    @Schema(description = "기본 사이클 타임", example = "10.5")
    private Float defaultCycleTime;

    @Schema(description = "적정 공정 재고 수량", example = "100.0")
    private Float optimalProgressInventoryQty;

    @Schema(description = "안전 공정 재고 수량", example = "50.0")
    private Float safetyProgressInventoryQty;

    @Schema(description = "공정 기본 사양", example = "표준 사양", maxLength = 200)
    private String progressDefaultSpec;

    @Schema(description = "주요 관리 내용", example = "품질 관리, 생산성 향상", maxLength = 1000)
    private String keyManagementContents;
}
