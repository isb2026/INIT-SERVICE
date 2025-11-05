package com.lts5.init.payload.request.progressvendor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressVendorUpdateAllRequest {

    private Long progressId;
    private Long vendorId;
    private BigDecimal unitCost;
    private BigDecimal quantity;
    private String unit;
    private Boolean isDefaultVendor;
    private String updateBy;
}