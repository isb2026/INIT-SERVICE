package com.lts5.init.payload.request.vendor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Vendor 검색 요청")
public class VendorSearchRequest {

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

    @Schema(description = "Comp code", example = "CODE001")
    private String compCode;

    @Schema(description = "Comp type", example = "COM-004-003")
    private String compType;

    @Schema(description = "License no", example = "12345")
    private String licenseNo;

    @Schema(description = "Comp name", example = "샘플명")
    private String compName;

    @Schema(description = "Ceo name", example = "샘플명")
    private String ceoName;

    @Schema(description = "Comp email", example = "샘플값")
    private String compEmail;

    @Schema(description = "Tel number", example = "12345")
    private String telNumber;

    @Schema(description = "Fax number", example = "12345")
    private String faxNumber;

    @Schema(description = "Zip code", example = "CODE001")
    private String zipCode;

    @Schema(description = "Address dtl", example = "샘플값")
    private String addressDtl;

    @Schema(description = "Address mst", example = "샘플값")
    private String addressMst;
}
