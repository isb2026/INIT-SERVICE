package com.lts5.init.payload.request.codegroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "CodeGroup Create 요청")
public class CodeGroupCreateRequest {

    @Schema(description = "parent code group id", example = "1")
    private Long parentId;

    @Schema(description = "is root", example = "1", type = "integer", format = "int32")
    private Boolean isRoot;

    @Size(max = 3, message = "group code ex) PRD, 001은 3자 이내로 입력해주세요.")
    @Schema(description = "group code ex) PRD, 001 (자동 생성 시 생략 가능)", example = "CODE001")
    private String groupCode;

    @NotBlank(message = "group name ex) 제품 분류은 필수 입력 항목입니다.")
    @Size(max = 100, message = "group name ex) 제품 분류은 100자 이내로 입력해주세요.")
    @Schema(description = "group name ex) 제품 분류", example = "샘플명")
    private String groupName;

    @Size(max = 255, message = "description은 255자 이내로 입력해주세요.")
    @Schema(description = "description", example = "샘플값")
    private String description;
}
