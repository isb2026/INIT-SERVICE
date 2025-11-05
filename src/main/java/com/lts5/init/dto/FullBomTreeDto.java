package com.lts5.init.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 전체 BOM 트리 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "전체 BOM 트리 응답")
public class FullBomTreeDto {

    @Schema(description = "루트 아이템들 (공정별 그룹화)", example = "[]")
    private List<RootItemTreeDto> rootItems;

    @Schema(description = "전체 BOM 개수", example = "150")
    private Long totalCount;

    @Schema(description = "루트 아이템 개수", example = "5")
    private Integer rootItemCount;
}
