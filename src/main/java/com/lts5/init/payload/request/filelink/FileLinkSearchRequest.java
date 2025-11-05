package com.lts5.init.payload.request.filelink;

import com.lts5.init.entity.enums.OwnerType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileLinkSearchRequest {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "사용여부", example = "true")
    private Boolean isUse;

    @Schema(description = "소유자 테이블명", example = "item")
    private String ownerTable;

    @Schema(description = "파일 종류", example = "ITEM_IMG")
    private OwnerType ownerType;

    @Schema(description = "소유자 ID", example = "1")
    private Long ownerId;

    @Schema(description = "파일 URL", example = "https://storage.com/file.jpg")
    private String url;

    @Schema(description = "정렬 순서", example = "1")
    private Short sortOrder;

    @Schema(description = "대표 여부", example = "false")
    private Boolean isPrimary;

    @Schema(description = "파일 설명", example = "대표 이미지")
    private String description;

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
}
