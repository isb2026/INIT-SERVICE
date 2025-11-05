package com.lts5.init.payload.request.terminal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Terminal 검색 요청")
public class TerminalSearchRequest {

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

    @Schema(description = "데이터 회계년", example = "1")
    private Short accountYear;

    @Schema(description = "Terminal code", example = "CODE001")
    private String terminalCode;

    @Schema(description = "Terminal name", example = "샘플명")
    private String terminalName;

    @Schema(description = "Description", example = "샘플값")
    private String description;

    @Schema(description = "Image url", example = "샘플값")
    private String imageUrl;
}
