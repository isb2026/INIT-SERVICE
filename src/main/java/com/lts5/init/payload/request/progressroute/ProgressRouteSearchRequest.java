package com.lts5.init.payload.request.progressroute;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ProgressRoute 검색 요청")
public class ProgressRouteSearchRequest {

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

    @Schema(description = "item id (FK)", example = "1")
    private Long itemId;

    @Schema(description = "progress ord", example = "1", type = "integer", format = "int32")
    private Byte progressSequence;

    @Schema(description = "progress type code", example = "CODE001")
    private String progressTypeCode;

    @Schema(description = "progress type name", example = "샘플명")
    private String progressTypeName;

    @Schema(description = "progress real name", example = "샘플명")
    private String progressRealName;

    @Schema(description = "default cycle time")
    private Double defaultCycleTime;

    @Schema(description = "lot size")
    private Double lotSize;

    @Schema(description = "lot unit", example = "샘플값")
    private String lotUnit;

    @Schema(description = "optimal_progress_inventory_level")
    private Double optimalProgressInventoryQty;

    @Schema(description = "safety_progress_inventory_level")
    private Double safetyProgressInventoryQty;

    @Schema(description = "progress default specification", example = "샘플값")
    private String progressDefaultSpec;

    @Schema(description = "key_management_contents", example = "샘플값")
    private String keyManagementContents;
}
