package com.lts5.init.controller;

import com.primes.library.common.response.CommonResponse;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.ProgressRouteDto;
import com.lts5.init.payload.request.progressroute.ProgressRouteCreateRequest;

import com.lts5.init.payload.request.progressroute.ProgressRouteUpdateRequest;
import com.lts5.init.payload.request.progressroute.ProgressRouteUpdateAllRequest;
import com.lts5.init.payload.request.progressroute.ProgressRouteSearchRequest;
import com.lts5.init.service.ProgressRouteService;
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
@RequestMapping("/progress-route")
@Validated
@RequiredArgsConstructor
@Tag(name = "ProgressRoute", description = "ProgressRoute 관리 API")
public class ProgressRouteController {
    private final ProgressRouteService progressRouteService;
    private final GlobalMapper globalMapper;

    @Operation(summary = "ProgressRoute 조회")
    @GetMapping
    public CommonResponse<Page<ProgressRouteDto>> search(
            @Valid ProgressRouteSearchRequest searchRequest,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer page,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return CommonResponse.createSuccess(progressRouteService.search(searchRequest, pageRequest));
    }

    @Operation(summary = "ProgressRoute 생성")
    @PostMapping
    public CommonResponse<List<ProgressRouteDto>> create(@Valid @RequestBody List<ProgressRouteCreateRequest> requests) {
        List<ProgressRouteDto> dtos = requests.stream()
            .map(request -> globalMapper.map(request, ProgressRouteDto.class))
            .toList();
        return CommonResponse.createSuccess(progressRouteService.createList(dtos));
    }

    @Operation(summary = "ProgressRoute 수정")
    @PutMapping("/{id}")
    public CommonResponse<ProgressRouteDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProgressRouteUpdateRequest request) {
        ProgressRouteDto dto = globalMapper.map(request, ProgressRouteDto.class);
        return CommonResponse.createSuccess(progressRouteService.update(id, dto));
    }

    @Operation(summary = "ProgressRoute 일괄 수정")
    @PutMapping
    public CommonResponse<List<ProgressRouteDto>> updateAll(
            @Valid @RequestBody List<ProgressRouteUpdateAllRequest> requests) {
        List<ProgressRouteDto> dtos = requests.stream()
            .map(request -> globalMapper.map(request, ProgressRouteDto.class))
            .toList();
        return CommonResponse.createSuccess(progressRouteService.updateAll(dtos));
    }

    @Operation(summary = "ProgressRoute 삭제")
    @DeleteMapping
    public CommonResponse<?> delete(@RequestBody List<Long> ids) {
        progressRouteService.delete(ids);
        return CommonResponse.createSuccessWithNoContent();
    }

    @Operation(summary = "ProgressRoute 특정 필드 값 전체 조회")
    @GetMapping("/fields/{fieldName}")
    public CommonResponse<List<?>> findAllFieldValues(
            @PathVariable String fieldName,
            @Valid ProgressRouteSearchRequest searchRequest) {
        return CommonResponse.createSuccess(progressRouteService.getFieldValues(fieldName, searchRequest));
    }
}