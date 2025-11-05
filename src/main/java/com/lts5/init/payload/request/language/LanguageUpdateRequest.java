package com.lts5.init.payload.request.language;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Language Update 요청")
public class LanguageUpdateRequest {

    @NotNull(message = "id는 필수 입력 항목입니다.")
    @Schema(description = "언어 ID", example = "1")
    private Long id;

    @Size(max = 50, message = "name은 50자 이내로 입력해주세요.")
    @Schema(description = "언어명", example = "Korean")
    private String name;

    @Size(max = 10, message = "isoCode는 10자 이내로 입력해주세요.")
    @Schema(description = "ISO 코드", example = "ko")
    private String isoCode;
} 