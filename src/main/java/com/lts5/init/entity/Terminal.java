package com.lts5.init.entity;

import com.lts5.init.dto.TerminalDto;
import com.primes.library.entity.BaseEntity;
import com.primes.library.entity.SnowflakeId;
import com.primes.library.entity.SnowflakeIdEntityListener;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@EntityListeners(SnowflakeIdEntityListener.class)
@Table(name = "terminals")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Terminal extends BaseEntity {

    @Id
    @SnowflakeId
    @Column(name = "terminal_id")
    private Long id;

    @Builder.Default
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Builder.Default
    @Column(name = "is_use")
    private Boolean isUse = true;

    @Transient
    private Short accountYear;

    @Column(name = "terminal_code", length = 10)
    private String terminalCode;

    @Column(name = "terminal_name", length = 30)
    private String terminalName;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url", length = 100)
    private String imageUrl;

    public void delete() {
        this.isDelete = true;
        this.isUse = false;
    }
}
