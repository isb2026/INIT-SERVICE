package com.lts5.init.entity;

import com.primes.library.entity.BaseEntity;
import com.primes.library.entity.SnowflakeId;
import com.primes.library.entity.SnowflakeIdEntityListener;
import lombok.Builder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@EntityListeners(SnowflakeIdEntityListener.class)
@Table(name = "mbom")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Mbom extends BaseEntity {

    @Id
    @SnowflakeId
    @Column(name = "mbom_id")
    private Long id;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Column(name = "parent_item_id")
    private Long parentItemId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Builder.Default
    @Column(name = "is_root")
    private Boolean isRoot = false;

    @Column(name = "parent_progress_id")
    private Long parentProgressId;

    @Column(name = "input_num")
    private Float inputNum;

    @Column(name = "item_progress_id")
    private Long itemProgressId;

    @Column(name = "input_unit_code", length = 20)
    private String inputUnitCode;

    @Column(name = "input_unit", length = 10)
    private String inputUnit;

    public void delete() {
        this.isDelete = true;
    }
}
