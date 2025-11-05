package com.lts5.init.dto;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MbomDto {

    private Long id;
    private Short tenantId;
    private Boolean isDelete;
    private Long parentItemId;
    private Long itemId;
    private Boolean isRoot;
    private Long parentProgressId;
    private Float inputNum;
    private Long itemProgressId;
    private String inputUnitCode;
    private String inputUnit;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
