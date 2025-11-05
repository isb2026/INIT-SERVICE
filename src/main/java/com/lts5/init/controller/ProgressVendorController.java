package com.lts5.init.controller;

import com.lts5.init.dto.ProgressVendorDto;
import com.lts5.init.payload.request.progressvendor.ProgressVendorCreateRequest;
import com.lts5.init.payload.request.progressvendor.ProgressVendorSearchRequest;
import com.lts5.init.payload.request.progressvendor.ProgressVendorUpdateAllRequest;
import com.lts5.init.payload.request.progressvendor.ProgressVendorUpdateRequest;
import com.lts5.init.service.ProgressVendorService;
import com.primes.library.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/progress-vendors")
@Validated
@RequiredArgsConstructor
@Tag(name = "ProgressVendor", description = "제조공정 외주업체 관리 API")
public class ProgressVendorController {

    private final ProgressVendorService progressVendorService;

    @Operation(summary = "ProgressVendor 조회")
    @GetMapping
    public CommonResponse<Page<ProgressVendorDto>> search(
            @Valid ProgressVendorSearchRequest searchRequest,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer page,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return CommonResponse.createSuccess(progressVendorService.searchProgressVendors(searchRequest, pageRequest));
    }

    @Operation(summary = "ProgressVendor 생성")
    @PostMapping
    public CommonResponse<List<ProgressVendorDto>> create(@Valid @RequestBody List<ProgressVendorCreateRequest> requests) {
        return CommonResponse.createSuccess(progressVendorService.createProgressVendors(requests));
    }

    @Operation(summary = "ProgressVendor 수정")
    @PutMapping("/{progressId}/{vendorId}")
    public CommonResponse<ProgressVendorDto> update(
            @PathVariable Long progressId,
            @PathVariable Long vendorId,
            @Valid @RequestBody ProgressVendorUpdateRequest request) {
        return CommonResponse.createSuccess(progressVendorService.update(progressId, vendorId, request));
    }

    @Operation(summary = "ProgressVendor 일괄 수정")
    @PutMapping
    public CommonResponse<List<ProgressVendorDto>> updateAll(
            @Valid @RequestBody List<ProgressVendorUpdateAllRequest> requests) {
        return CommonResponse.createSuccess(progressVendorService.updateAll(requests));
    }

    @Operation(summary = "ProgressVendor 삭제")
    @DeleteMapping("/{progressId}")
    public CommonResponse<?> delete(
            @PathVariable Long progressId,
            @RequestBody List<Long> vendorIds) {
        progressVendorService.delete(progressId, vendorIds);
        return CommonResponse.createSuccessWithNoContent();
    }

    @Operation(summary = "특정 공정의 업체 목록 조회")
    @GetMapping("/item-progress/{progressId}/vendors")
    public CommonResponse<List<ProgressVendorDto>> getVendorsByItemProgress(
            @PathVariable Long progressId) {
        return CommonResponse.createSuccess(progressVendorService.getVendorsByItemProgressId(progressId));
    }

    @Operation(summary = "특정 공정의 기본 업체 조회")
    @GetMapping("/item-progress/{progressId}/default-vendors")
    public CommonResponse<List<ProgressVendorDto>> getDefaultVendorsByItemProgress(
            @PathVariable Long progressId) {
        return CommonResponse.createSuccess(progressVendorService.getDefaultVendorsByProgressId(progressId));
    }
} 