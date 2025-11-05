package com.lts5.init.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dataset_version")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetVersion {

    @Id
    @Column(name = "current_version", nullable = false)
    private Integer currentVersion;
} 