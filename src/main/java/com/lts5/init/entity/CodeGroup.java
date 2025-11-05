package com.lts5.init.entity;

import com.primes.library.entity.BaseEntity;
import com.primes.library.entity.SnowflakeId;
import com.primes.library.entity.SnowflakeIdEntityListener;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@EntityListeners(SnowflakeIdEntityListener.class)
@Table(name = "code_groups")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeGroup {

    @Id
    @SnowflakeId
    @Column(name = "code_group_id")
    private Long id;

    @Column(name = "t_id")
    private Short tenantId;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Builder.Default
    @Column(name = "is_use")
    private Boolean isUse = true;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "is_root", nullable = false)
    private Boolean isRoot;

    @Column(name = "group_code", length = 50, nullable = false)
    private String groupCode;

    @Column(name = "group_name", length = 100, nullable = false)
    private String groupName;

    @Column(name = "description", length = 255)
    private String description;

    @CreatedDate
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "create_by")
    private String createdBy;

    @LastModifiedDate
    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "update_by")
    private String updatedBy;

    @JsonIgnore
    @OneToMany(mappedBy = "codeGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Code> codes;

    public void delete() {
        this.isDelete = true;
        this.isUse = false;
    }
}
