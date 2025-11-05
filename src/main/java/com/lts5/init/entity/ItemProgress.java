package com.lts5.init.entity;

import com.primes.library.entity.BaseEntity;
import com.primes.library.entity.SnowflakeId;
import com.primes.library.entity.SnowflakeIdEntityListener;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@EntityListeners(SnowflakeIdEntityListener.class)
@Table(name = "item_progress")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ItemProgress extends BaseEntity {

    @Id
    @SnowflakeId
    @Column(name = "item_progress_id")
    private Long id;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Builder.Default
    @Column(name = "is_use")
    private Boolean isUse = true;

    @Column(name = "item_id")
    private Long itemId;

    @Transient
    private Short accountYear;

    @Column(name = "progress_order")
    private Byte progressOrder;

    @Column(name = "progress_name", length = 100, nullable = false)
    private String progressName;

    @Builder.Default
    @Column(name = "is_outsourcing")
    private Boolean isOutsourcing = true;

    @Column(name = "progress_type_code", length = 3)
    private String progressTypeCode;

    @Column(name = "progress_type_name", length = 100)
    private String progressTypeName;

    @Column(name = "unit_weight")
    private Float unitWeight;

    @Column(name = "unit_type_name", length = 100)
    private String unitTypeName;

    @Column(name = "unit_type_code", length = 50)
    private String unitTypeCode;

    @Column(name = "default_cycle_time")
    private Float defaultCycleTime;

    @Column(name = "optimal_progress_inventory_qty")
    private Float optimalProgressInventoryQty;

    @Column(name = "safety_progress_inventory_qty")
    private Float safetyProgressInventoryQty;

    @Column(name = "progress_default_spec", length = 200)
    private String progressDefaultSpec;

    @Column(name = "key_management_contents", length = 1000)
    private String keyManagementContents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    @JsonIgnore
    private Item item;

    @JsonIgnore
    @OneToMany(mappedBy = "itemProgress", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProgressVendor> progressVendors;

    public void setDelete() {
        this.isDelete = true;
        this.isUse = false;
    }
}
