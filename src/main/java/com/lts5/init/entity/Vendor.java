package com.lts5.init.entity;

import com.lts5.init.dto.VendorDto;
import com.primes.library.entity.BaseEntity;
import com.primes.library.entity.SnowflakeId;
import com.primes.library.entity.SnowflakeIdEntityListener;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@EntityListeners(SnowflakeIdEntityListener.class)
@Table(name = "vendors")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Vendor extends BaseEntity {

    @Id
    @SnowflakeId
    @Column(name = "vendor_id")
    private Long id;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Builder.Default
    @Column(name = "is_use")
    private Boolean isUse = true;

    @Column(name = "comp_code", length = 5)
    private String compCode;

    @Column(name = "comp_type", length = 50)
    private String compType;

    @Column(name = "license_no", length = 20)
    private String licenseNo;

    @Column(name = "comp_name", length = 255)
    private String compName;

    @Column(name = "ceo_name", length = 20)
    private String ceoName;

    @Column(name = "comp_email", length = 45)
    private String compEmail;

    @Column(name = "tel_number", length = 20)
    private String telNumber;

    @Column(name = "fax_number", length = 20)
    private String faxNumber;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "address_dtl", length = 255)
    private String addressDtl;

    @Column(name = "address_mst", length = 255)
    private String addressMst;

    @JsonIgnore
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProgressVendor> progressVendors;

    public void delete() {
        this.isDelete = true;
        this.isUse = false;
    }
}
