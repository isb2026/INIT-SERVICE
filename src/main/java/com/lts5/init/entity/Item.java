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
@Table(name = "items")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity {

    @Id
    @SnowflakeId
    @Column(name = "item_id")
    private Long id;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Builder.Default
    @Column(name = "is_use")
    private Boolean isUse = true;

    @Column(name = "item_no", nullable = false)
    private Short itemNo;

    @Column(name = "item_number", length = 45)
    private String itemNumber;

    @Column(name = "item_name", length = 255)
    private String itemName;

    @Column(name = "item_spec", length = 255)
    private String itemSpec;

    @Column(name = "item_model", length = 45)
    private String itemModel;

    @Column(name = "item_type1", length = 50)
    private String itemType1;

    @Column(name = "item_type2", length = 50)
    private String itemType2;

    @Column(name = "item_type3", length = 50)
    private String itemType3;

    @Column(name = "item_unit", length = 3)
    private String itemUnit;

    @Column(name = "lot_size", length = 50)
    private String lotSize;

    @Column(name = "optimal_inventory_qty")
    private Double optimalInventoryQty;

    @Column(name = "safety_inventory_qty")
    private Double safetyInventoryQty;

    @JsonIgnore
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemProgress> itemProgress;

    @JsonIgnore
    @Transient  // Temporarily disabled: progress_route table doesn't exist
    private List<ProgressRoute> progressRoutes;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "item_id")
    private List<FileLink> fileLinks;

    public void delete() {
        this.isDelete = true;
        this.isUse = false;
    }
}
