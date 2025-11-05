package com.lts5.init.payload.request.item;

import com.lts5.init.payload.request.filelink.FileLinkUpdateInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Item 일괄 Update 요청")
public class ItemUpdateAllRequest {

    @NotNull(message = "ID는 필수입니다.")
    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "사용여부", example = "true")
    private Boolean isUse;

    @Schema(description = "아이템번호", example = "1")
    private Short itemNo;

    @Size(max = 45, message = "품번은 45자 이내로 입력해주세요.")
    @Schema(description = "품번", example = "12345")
    private String itemNumber;

    @Size(max = 255, message = "품명은 255자 이내로 입력해주세요.")
    @Schema(description = "품명", example = "샘플명")
    private String itemName;

    @Size(max = 255, message = "규격은 255자 이내로 입력해주세요.")
    @Schema(description = "규격", example = "샘플값")
    private String itemSpec;

    @Size(max = 45, message = "모델명은 45자 이내로 입력해주세요.")
    @Schema(description = "모델명", example = "샘플값")
    private String itemModel;

    @Size(max = 50, message = "제품대분류는 50자 이내로 입력해주세요.")
    @Schema(description = "제품대분류", example = "샘플값")
    private String itemType1Code;

    @Size(max = 50, message = "제품중분류는 50자 이내로 입력해주세요.")
    @Schema(description = "제품중분류", example = "샘플값")
    private String itemType2Code;

    @Size(max = 50, message = "제품소분류는 50자 이내로 입력해주세요.")
    @Schema(description = "제품소분류", example = "샘플값")
    private String itemType3Code;

    @Size(max = 3, message = "단위는 3자 이내로 입력해주세요.")
    @Schema(description = "단위", example = "샘플값")
    private String itemUnit;

    @Size(max = 50, message = "Lot 사이즈는 50자 이내로 입력해주세요.")
    @Schema(description = "Lot 사이즈", example = "샘플값")
    private String lotSizeCode;

    @Schema(description = "optimal_inventory_level (적정재고량)", example = "100.50")
    private Double optimalInventoryQty;

    @Schema(description = "safety_inventory_level(안전재고량)", example = "100.50")
    private Double safetyInventoryQty;

    @Schema(description = "파일 링크 목록")
    private List<FileLinkUpdateInfo> fileUrls;
}
