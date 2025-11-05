package com.lts5.init.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressVendorDto {

    private Long progressId;
    private Long vendorId;
    private BigDecimal unitCost;
    private BigDecimal quantity;
    private String unit;
    private Boolean isDefaultVendor;
    private String createBy;
    private LocalDateTime createAt;
    private String updateBy;
    private LocalDateTime updateAt;

    // 관계 정보를 위한 추가 필드들
    private ItemProgressDto itemProgress;
    
    // Vendor 정보를 평면적으로 포함
    private Short tenantId;
    private Boolean isDelete;
    private Boolean isUse;
    private String compCode;
    private String compType;
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