package com.lts5.init.payload.request.language;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Language Create 요청")
public class LanguageCreateRequest {

    @NotBlank(message = "name은 필수 입력 항목입니다.")
    @Size(max = 50, message = "name은 50자 이내로 입력해주세요.")
    @Schema(description = "언어명", example = "Korean")
    private String name;

    @NotBlank(message = "isoCode는 필수 입력 항목입니다.")
    @Size(max = 10, message = "isoCode는 10자 이내로 입력해주세요.")
    @Schema(description = "ISO 코드", example = "ko")
    private String isoCode;
} 