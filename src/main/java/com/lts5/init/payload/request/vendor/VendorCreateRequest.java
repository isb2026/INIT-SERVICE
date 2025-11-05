package com.lts5.init.payload.request.vendor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Vendor Create 요청")
public class VendorCreateRequest {

    @Size(max = 50, message = "Comp type은 50자 이내로 입력해주세요.")
    @Schema(description = "Comp type", example = "COM-004-003")
    private String compType;

    @Size(max = 20, message = "License no은 20자 이내로 입력해주세요.")
    @Schema(description = "License no", example = "12345")
    private String licenseNo;

    @Size(max = 255, message = "Comp name은 255자 이내로 입력해주세요.")
    @Schema(description = "Comp name", example = "샘플명")
    private String compName;

    @Size(max = 20, message = "Ceo name은 20자 이내로 입력해주세요.")
    @Schema(description = "Ceo name", example = "샘플명")
    private String ceoName;

    @Size(max = 45, message = "Comp email은 45자 이내로 입력해주세요.")
    @Schema(description = "Comp email", example = "샘플값")
    private String compEmail;

    @Size(max = 20, message = "Tel number은 20자 이내로 입력해주세요.")
    @Schema(description = "Tel number", example = "12345")
    private String telNumber;

    @Size(max = 20, message = "Fax number은 20자 이내로 입력해주세요.")
    @Schema(description = "Fax number", example = "12345")
    private String faxNumber;

    @Size(max = 10, message = "Zip code은 10자 이내로 입력해주세요.")
    @Schema(description = "Zip code", example = "CODE001")
    private String zipCode;

    @Size(max = 255, message = "Address dtl은 255자 이내로 입력해주세요.")
    @Schema(description = "Address dtl", example = "샘플값")
    private String addressDtl;

    @Size(max = 255, message = "Address mst은 255자 이내로 입력해주세요.")
    @Schema(description = "Address mst", example = "샘플값")
    private String addressMst;
}
