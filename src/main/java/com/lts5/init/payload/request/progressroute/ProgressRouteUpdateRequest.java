package com.lts5.init.payload.request.progressroute;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Size;
import java.lang.Boolean;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ProgressRoute Update 요청")
public class ProgressRouteUpdateRequest {

    @Schema(description = "is use", example = "true")
    private Boolean isUse;

    @Schema(description = "item id (FK)", example = "1")
    private Long itemId;

    @Schema(description = "progress ord", example = "1", type = "integer", format = "int32")
    private Byte progressSequence;

    @Size(max = 3, message = "progress type code은 3자 이내로 입력해주세요.")
    @Schema(description = "progress type code", example = "CODE001")
    private String progressTypeCode;

    @Size(max = 100, message = "progress type name은 100자 이내로 입력해주세요.")
    @Schema(description = "progress type name", example = "샘플명")
    private String progressTypeName;

    @Size(max = 100, message = "progress real name은 100자 이내로 입력해주세요.")
    @Schema(description = "progress real name", example = "샘플명")
    private String progressRealName;

    @Schema(description = "default cycle time")
    private Double defaultCycleTime;

    @Schema(description = "lot size")
    private Double lotSize;

    @Size(max = 10, message = "lot unit은 10자 이내로 입력해주세요.")
    @Schema(description = "lot unit", example = "샘플값")
    private String lotUnit;

    @Schema(description = "optimal_progress_inventory_level")
    private Double optimalProgressInventoryQty;

    @Schema(description = "safety_progress_inventory_level")
    private Double safetyProgressInventoryQty;

    @Size(max = 200, message = "progress default specification은 200자 이내로 입력해주세요.")
    @Schema(description = "progress default specification", example = "샘플값")
    private String progressDefaultSpec;

    @Schema(description = "key_management_contents", example = "샘플값")
    private String keyManagementContents;
}
