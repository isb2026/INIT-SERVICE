package com.lts5.init.entity;

import com.lts5.init.dto.ProgressVendorDto;
import com.primes.library.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "progress_vendor")
@IdClass(ProgressVendorId.class)
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressVendor {

    @Id
    @Column(name = "progress_id")
    private Long progressId;

    @Id
    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "unit_cost", precision = 18, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "quantity", precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit", length = 10)
    private String unit;

    @Builder.Default
    @Column(name = "is_default_vendor")
    private Boolean isDefaultVendor = false;

    @Column(name = "create_by", length = 30)
    private String createBy;

    @Column(name = "create_at")
    private java.time.LocalDateTime createAt;

    @Column(name = "update_by", length = 30)
    private String updateBy;

    @Column(name = "update_at")
    private java.time.LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progress_id", insertable = false, updatable = false)
    @JsonIgnore
    private ItemProgress itemProgress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", insertable = false, updatable = false)
    @JsonIgnore
    private Vendor vendor;

    @PrePersist
    public void prePersist() {
        this.createAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = java.time.LocalDateTime.now();
    }
} 