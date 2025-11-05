package com.lts5.init.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfo {
    
    private Long id;                // 아이템 ID
    private Short itemNo;           // 아이템 번호
    private String itemNumber;      // 아이템 넘버
    private String itemName;        // 아이템명
    private String itemSpec;        // 아이템 스펙
    private String itemModel;       // 아이템 모델
    private String itemType1;       // 아이템 타입1
    private String itemType2;       // 아이템 타입2
    private String itemType3;       // 아이템 타입3
    private String itemUnit;        // 아이템 단위
    
    // 재고 관련 정보
    private String lotSize;                    // 로트 사이즈
    private Double optimalInventoryQty;       // 최적 재고량
    private Double safetyInventoryQty;        // 안전 재고량
    
    // 상태 정보
    private Boolean isUse;          // 사용 여부
}
