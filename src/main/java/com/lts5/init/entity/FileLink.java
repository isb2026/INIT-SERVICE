package com.lts5.init.entity;

import com.lts5.init.entity.enums.OwnerType;
import com.primes.library.entity.BaseEntity;
import com.primes.library.entity.SnowflakeId;
import com.primes.library.entity.SnowflakeIdEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@EntityListeners(SnowflakeIdEntityListener.class)
@Table(name = "file_link")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FileLink extends BaseEntity {

    @Id
    @SnowflakeId
    @Column(name = "file_link_id")
    private Long id;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Builder.Default
    @Column(name = "is_use")
    private Boolean isUse = true;

    @Column(name = "owner_table", length = 40)
    private String ownerTable;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", length = 40)
    private OwnerType ownerType;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "url", length = 255, nullable = false)
    private String url;

    @Builder.Default
    @Column(name = "sort_order")
    private Short sortOrder = 1;

    @Builder.Default
    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "description", length = 255)
    private String description;

    public void delete() {
        this.isDelete = true;
        this.isUse = false;
    }
}