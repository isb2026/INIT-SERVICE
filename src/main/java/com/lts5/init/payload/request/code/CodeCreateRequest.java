package com.lts5.init.payload.request.code;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Code Create 요청")
public class CodeCreateRequest {

    @Schema(description = "FK", example = "1")
    private Long codeGroupId;

    @NotBlank(message = "code name ex) 베어링류은 필수 입력 항목입니다.")
    @Size(max = 100, message = "code name ex) 베어링류은 100자 이내로 입력해주세요.")
    @Schema(description = "code name ex) 베어링류", example = "CODE001")
    private String codeName;

    @Size(max = 255, message = "description은 255자 이내로 입력해주세요.")
    @Schema(description = "description", example = "샘플값")
    private String description;
}
