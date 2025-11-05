package com.lts5.init.payload.request.filelink;

import com.lts5.init.entity.enums.OwnerType;
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
@Schema(description = "FileLink 정보")
public class FileLinkUpdateInfo {
    @Schema(description = "FileLink ID", example = "1")
    private Long id;

    @NotNull(message = "파일 URL은 필수입니다.")
    @Size(max = 255, message = "파일 URL은 255자 이내로 입력해주세요.")
    @Schema(description = "파일 URL", example = "https://example.com/file.jpg")
    private String url;

    @NotNull(message = "소유자 타입은 필수입니다.")
    @Schema(description = "소유자 타입", example = "ITEM_IMG", allowableValues = {"ITEM_IMG", "ITEM_DESIGN"})
    private OwnerType ownerType;

    @Schema(description = "정렬 순서", example = "1")
    private Short sortOrder;

    @Schema(description = "대표 이미지 여부", example = "false")
    private Boolean isPrimary;

    @Size(max = 255, message = "설명은 255자 이내로 입력해주세요.")
    @Schema(description = "설명", example = "제품 이미지")
    private String description;
}