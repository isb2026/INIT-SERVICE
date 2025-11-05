package com.lts5.init.controller;

import com.lts5.init.payload.response.item.ItemSearchResponse;
import com.primes.library.common.response.CommonResponse;
import com.lts5.init.dto.ItemDto;
import com.lts5.init.payload.request.item.ItemCreateRequest;

import com.lts5.init.payload.request.item.ItemUpdateRequest;
import com.lts5.init.payload.request.item.ItemUpdateAllRequest;
import com.lts5.init.payload.request.item.ItemSearchRequest;
import com.lts5.init.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item")
@Validated
@RequiredArgsConstructor
@Tag(name = "Item", description = "Item 관리 API")
public class ItemController {
    private final ItemService itemService;

    @Operation(summary = "Item 조회")
    @GetMapping
    public CommonResponse<Page<ItemSearchResponse>> search(
            @Valid ItemSearchRequest searchRequest,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer page,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return CommonResponse.createSuccess(itemService.search(searchRequest, pageRequest));
    }

    @Operation(summary = "Item 전체 조회", description = "페이징 없이 전체 Item 리스트를 조회합니다.")
    @GetMapping("/all")
    public CommonResponse<List<ItemSearchResponse>> findAll(@Valid ItemSearchRequest searchRequest) {
        return CommonResponse.createSuccess(itemService.findAll(searchRequest));
    }

    @Operation(summary = "Item 생성", description = "**사용 가능한 OwnerType 값:**\n" +
                                                    "- `ITEM_IMG`: 아이템 이미지\n" +
                                                    "- `ITEM_DESIGN`: 아이템 도면\n" +
                                                    "- `ITEM_PROGRESS_DESIGN`: 아이템 공정 도면\n" +
                                                    "- `MACHINE_IMG`: 설비 이미지\n" +
                                                    "- `MACHINE_INSPECTION_IMG`: 설비 일상점검 이미지\n" +
                                                    "- `MOLD_DESIGN`: 금형 도면")
    @PostMapping
    public CommonResponse<List<ItemDto>> create(@Valid @RequestBody List<ItemCreateRequest> requests) {
        return CommonResponse.createSuccess(itemService.createList(requests));
    }

    @Operation(summary = "Item 수정")
    @PutMapping("/{id}")
    public CommonResponse<ItemDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemUpdateRequest request) {
        return CommonResponse.createSuccess(itemService.update(id, request));
    }

    @Operation(summary = "Item 일괄 수정")
    @PutMapping
    public CommonResponse<List<ItemDto>> updateAll(
            @Valid @RequestBody List<ItemUpdateAllRequest> requests) {
        return CommonResponse.createSuccess(itemService.updateAll(requests));
    }

    @Operation(summary = "Item 삭제")
    @DeleteMapping
    public CommonResponse<?> delete(@RequestBody List<Long> ids) {
        itemService.delete(ids);
        return CommonResponse.createSuccessWithNoContent();
    }

    @Operation(summary = "Item 특정 필드 값 전체 조회")
    @GetMapping("/fields/{fieldName}")
    public CommonResponse<List<?>> findAllFieldValues(
            @PathVariable String fieldName,
            @Valid ItemSearchRequest searchRequest) {
        return CommonResponse.createSuccess(itemService.getFieldValues(fieldName, searchRequest));
    }
}