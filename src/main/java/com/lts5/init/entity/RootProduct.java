package com.lts5.init.entity;

import com.lts5.init.dto.RootProductDto;
import com.primes.library.entity.BaseEntity;
import com.primes.library.entity.SnowflakeId;
import com.primes.library.entity.SnowflakeIdEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 루트 제품 엔티티
 */
@Entity
@EntityListeners(SnowflakeIdEntityListener.class)
@Table(name = "root_products")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RootProduct extends BaseEntity {

    @Id
    @SnowflakeId
    @Column(name = "root_product_id")
    private Long id;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_code", length = 50)
    private String productCode;

    @Column(name = "description", length = 500)
    private String description;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    public void delete() {
        this.isDelete = true;
    }
}
