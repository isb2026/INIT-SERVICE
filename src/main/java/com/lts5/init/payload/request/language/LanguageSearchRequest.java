package com.lts5.init.payload.request.language;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Language 검색 요청")
public class LanguageSearchRequest {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "언어명", example = "Korean")
    private String name;

    @Schema(description = "ISO 코드", example = "ko")
    private String isoCode;
} 