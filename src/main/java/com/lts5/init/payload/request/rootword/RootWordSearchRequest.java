package com.lts5.init.payload.request.rootword;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "RootWord 검색 요청")
public class RootWordSearchRequest {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "기본 단어", example = "사과")
    private String word;

    @Schema(description = "언어 ID", example = "1")
    private Long languageId;
} 