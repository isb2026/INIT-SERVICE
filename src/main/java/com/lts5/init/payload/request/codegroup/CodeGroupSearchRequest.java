package com.lts5.init.payload.request.codegroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "CodeGroup 검색 요청")
public class CodeGroupSearchRequest {

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

    @Schema(description = "parent code group id", example = "1")
    private Long parentId;

    @Schema(description = "is root", example = "1", type = "integer", format = "int32")
    private Byte isRoot;

    @Schema(description = "group code ex) PRD, 001", example = "CODE001")
    private String groupCode;

    @Schema(description = "group name ex) 제품 분류", example = "샘플명")
    private String groupName;

    @Schema(description = "description", example = "샘플값")
    private String description;
}
