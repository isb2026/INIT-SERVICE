package com.lts5.init.dto;

import com.lts5.init.entity.Vendor;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VendorDto {

    private Long id;
    private Short tenantId;
    private Boolean isDelete;
    private Boolean isUse;
    private String compCode;
    private String compType;
    private String compTypeName; // 회사 유형명 (Code 테이블에서 조회)
    private String licenseNo;
    private String compName;
    private String ceoName;
    private String compEmail;
    private String telNumber;
    private String faxNumber;
    private String zipCode;
    private String addressDtl;
    private String addressMst;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
