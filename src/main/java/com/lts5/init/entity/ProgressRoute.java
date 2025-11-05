package com.lts5.init.entity;

import com.lts5.init.dto.ProgressRouteDto;
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
@Table(name = "progress_route")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressRoute extends BaseEntity {

    @Id
    @SnowflakeId
    @Column(name = "progress_route_id")
    private Long id;

    @Column(name = "item_id")
    private Long itemId;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Builder.Default
    @Column(name = "is_use")
    private Boolean isUse = true;

    @Column(name = "progress_sequence")
    private Byte progressSequence;

    @Column(name = "progress_type_code", length = 3)
    private String progressTypeCode;

    @Column(name = "progress_type_name", length = 100)
    private String progressTypeName;

    @Column(name = "progress_real_name", length = 100)
    private String progressRealName;

    @Column(name = "default_cycle_time")
    private Double defaultCycleTime;

    @Column(name = "lot_size")
    private Double lotSize;

    @Column(name = "lot_unit", length = 10)
    private String lotUnit;

    @Column(name = "optimal_progress_inventory_qty")
    private Double optimalProgressInventoryQty;

    @Column(name = "safety_progress_inventory_qty")
    private Double safetyProgressInventoryQty;

    @Column(name = "progress_default_spec", length = 200)
    private String progressDefaultSpec;

    @Column(name = "key_management_contents")
    private String keyManagementContents;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    @JsonIgnore
    private Item item;

    public void delete() {
        this.isDelete = true;
        this.isUse = false;
    }
}
