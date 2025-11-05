package com.lts5.init.payload.request.rootword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "RootWord Create 요청")
public class RootWordCreateRequest {

    @NotBlank(message = "word는 필수 입력 항목입니다.")
    @Size(max = 255, message = "word는 255자 이내로 입력해주세요.")
    @Schema(description = "기본 단어", example = "사과")
    private String word;

    @NotNull(message = "languageId는 필수 입력 항목입니다.")
    @Schema(description = "언어 ID", example = "1")
    private Long languageId;
} 