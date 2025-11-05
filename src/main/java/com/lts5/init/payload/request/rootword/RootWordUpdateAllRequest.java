package com.lts5.init.payload.request.rootword;

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
@Schema(description = "RootWord 일괄 Update 요청")
public class RootWordUpdateAllRequest {

    @NotNull(message = "ID는 필수입니다.")
    @Schema(description = "ID", example = "1")
    private Long id;

    @Size(max = 255, message = "word는 255자 이내로 입력해주세요.")
    @Schema(description = "기본 단어", example = "사과")
    private String word;

    @Schema(description = "언어 ID", example = "1")
    private Long languageId;
} 