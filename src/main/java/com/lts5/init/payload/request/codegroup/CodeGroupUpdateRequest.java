package com.lts5.init.payload.request.codegroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Size;
import java.lang.Boolean;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "CodeGroup Update 요청")
public class CodeGroupUpdateRequest {

    @Schema(description = "use state", example = "true")
    private Boolean isUse;

    @Schema(description = "parent code group id", example = "1")
    private Long parentId;

    @Schema(description = "is root", example = "1", type = "integer", format = "int32")
    private Boolean isRoot;

    @Size(max = 100, message = "group name ex) 제품 분류은 100자 이내로 입력해주세요.")
    @Schema(description = "group name ex) 제품 분류", example = "샘플명")
    private String groupName;

    @Size(max = 255, message = "description은 255자 이내로 입력해주세요.")
    @Schema(description = "description", example = "샘플값")
    private String description;
}
