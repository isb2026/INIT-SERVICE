package com.lts5.init.payload.request.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Item 검색 요청")
public class ItemSearchRequest {

    @Schema(description = "사용 여부", example = "true")
    private Boolean isUse;

    @Schema(description = "생성일시 시작", example = "2024-01-01T00:00:00")
    private String createdAtStart;

    @Schema(description = "생성일시 종료", example = "2024-12-31T23:59:59")
    private String createdAtEnd;

    @Schema(description = "생성자", example = "admin")
    private String createdBy;

    @Schema(description = "수정일시 시작", example = "2024-01-01T00:00:00")
    private String updatedAtStart;

    @Schema(description = "수정일시 종료", example = "2024-12-31T23:59:59")
    private String updatedAtEnd;

    @Schema(description = "수정자", example = "admin")
    private String updatedBy;

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "아이템번호", example = "1")
    private Short itemNo;

    @Schema(description = "품번 ", example = "12345")
    private String itemNumber;

    @Schema(description = "품명", example = "샘플명")
    private String itemName;

    @Schema(description = "규격", example = "샘플값")
    private String itemSpec;

    @Schema(description = "모델명", example = "샘플값")
    private String itemModel;

    @Schema(description = "제품대분류", example = "샘플값")
    private String itemType1Code;

    @Schema(description = "제품중분류", example = "샘플값")
    private String itemType2Code;

    @Schema(description = "제품소분류", example = "샘플값")
    private String itemType3Code;

    @Schema(description = "단위", example = "샘플값")
    private String itemUnit;

    @Schema(description = "Lot 사이즈", example = "샘플값")
    private String lotSizeCode;

    @Schema(description = "optimal_inventory_level (적정재고량)")
    private Double optimalInventoryQty;

    @Schema(description = "safety_inventory_level(안전재고량)")
    private Double safetyInventoryQty;
}
