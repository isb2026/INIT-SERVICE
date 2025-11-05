package com.lts5.init.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OwnerType {
    ITEM_IMG("아이템 이미지"),
    ITEM_DESIGN("아이템 도면"),
    ITEM_PROGRESS_DESIGN("아이템 공정 도면"),
    MACHINE_IMG("설비 이미지"),
    MACHINE_INSPECTION_IMG("설비 일상점검 이미지"),
    MOLD_DESIGN("금형 도면");

    private final String description;
}