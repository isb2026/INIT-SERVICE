package com.lts5.init.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationType {
    CREATE("INSERT"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    UNKNOWN("UNKNOWN");

    private final String description;
}
