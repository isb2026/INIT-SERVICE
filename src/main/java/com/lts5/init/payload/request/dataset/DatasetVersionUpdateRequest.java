package com.lts5.init.payload.request.dataset;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DatasetVersion Update 요청")
public class DatasetVersionUpdateRequest {

    @Min(value = 0, message = "currentVersion은 0 이상의 정수여야 합니다.")
    @Schema(description = "현재 버전", example = "1")
    private int currentVersion;
} 