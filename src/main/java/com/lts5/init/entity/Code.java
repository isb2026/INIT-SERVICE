package com.lts5.init.entity;

import com.primes.library.entity.BaseEntity;
import com.primes.library.entity.SnowflakeId;
import com.primes.library.entity.SnowflakeIdEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@EntityListeners(SnowflakeIdEntityListener.class)
@Table(name = "codes")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Code extends BaseEntity {

    @Id
    @SnowflakeId
    @Column(name = "code_id")
    private Long id;

    @Column(name = "code_group_id")
    private Long codeGroupId;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Builder.Default
    @Column(name = "is_use")
    private Boolean isUse = true;

    @Column(name = "code_value", length = 50, nullable = false)
    private String codeValue;

    @Column(name = "code_name", length = 100, nullable = false)
    private String codeName;

    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_group_id", insertable = false, updatable = false)
    @JsonIgnore
    private CodeGroup codeGroup;

    public void delete() {
        this.isDelete = true;
        this.isUse = false;
    }
}
