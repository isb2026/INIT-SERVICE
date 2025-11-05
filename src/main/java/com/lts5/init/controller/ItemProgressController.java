package com.lts5.init.controller;

import com.primes.library.common.response.CommonResponse;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.ItemProgressDto;
import com.lts5.init.payload.request.itemprogress.ItemProgressCreateRequest;

import com.lts5.init.payload.request.itemprogress.ItemProgressUpdateRequest;
import com.lts5.init.payload.request.itemprogress.ItemProgressUpdateAllRequest;
import com.lts5.init.payload.request.itemprogress.ItemProgressSearchRequest;
import com.lts5.init.service.ItemProgressService;
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
@RequestMapping("/item-progress")
@Validated
@RequiredArgsConstructor
@Tag(name = "ItemProgress", description = "ItemProgress 관리 API")
public class ItemProgressController {
    private final ItemProgressService itemProgressService;
    private final GlobalMapper globalMapper;

    @Operation(summary = "ItemProgress 조회")
    @GetMapping
    public CommonResponse<Page<ItemProgressDto>> search(
            @Valid ItemProgressSearchRequest searchRequest,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer page,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return CommonResponse.createSuccess(itemProgressService.search(searchRequest, pageRequest));
    }

    @Operation(summary = "ItemProgress 생성")
    @PostMapping
    public CommonResponse<List<ItemProgressDto>> create(@Valid @RequestBody List<ItemProgressCreateRequest> requests) {
        List<ItemProgressDto> dtos = requests.stream()
            .map(request -> globalMapper.map(request, ItemProgressDto.class))
            .toList();
        return CommonResponse.createSuccess(itemProgressService.createList(dtos));
    }

    @Operation(summary = "ItemProgress 수정")
    @PutMapping("/{id}")
    public CommonResponse<ItemProgressDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ItemProgressUpdateRequest request) {
        ItemProgressDto dto = globalMapper.map(request, ItemProgressDto.class);
        return CommonResponse.createSuccess(itemProgressService.update(id, dto));
    }

    @Operation(summary = "ItemProgress 일괄 수정")
    @PutMapping
    public CommonResponse<List<ItemProgressDto>> updateAll(
            @Valid @RequestBody List<ItemProgressUpdateAllRequest> requests) {
        List<ItemProgressDto> dtos = requests.stream()
            .map(request -> globalMapper.map(request, ItemProgressDto.class))
            .toList();
        return CommonResponse.createSuccess(itemProgressService.updateAll(dtos));
    }

    @Operation(summary = "ItemProgress 삭제")
    @DeleteMapping
    public CommonResponse<?> delete(@RequestBody List<Long> ids) {
        itemProgressService.delete(ids);
        return CommonResponse.createSuccessWithNoContent();
    }

    @Operation(summary = "ItemProgress 특정 필드 값 전체 조회")
    @GetMapping("/fields/{fieldName}")
    public CommonResponse<List<?>> findAllFieldValues(
            @PathVariable String fieldName,
            @Valid ItemProgressSearchRequest searchRequest) {
        return CommonResponse.createSuccess(itemProgressService.getFieldValues(fieldName, searchRequest));
    }
}