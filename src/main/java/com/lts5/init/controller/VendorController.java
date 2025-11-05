package com.lts5.init.controller;

import com.primes.library.common.response.CommonResponse;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.VendorDto;
import com.lts5.init.payload.request.vendor.VendorCreateRequest;

import com.lts5.init.payload.request.vendor.VendorUpdateRequest;
import com.lts5.init.payload.request.vendor.VendorUpdateAllRequest;
import com.lts5.init.payload.request.vendor.VendorSearchRequest;
import com.lts5.init.service.VendorService;
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
@RequestMapping("/vendor")
@Validated
@RequiredArgsConstructor
@Tag(name = "Vendor", description = "Vendor 관리 API")
public class VendorController {
    private final VendorService vendorService;
    private final GlobalMapper globalMapper;

    @Operation(summary = "Vendor 조회")
    @GetMapping
    public CommonResponse<Page<VendorDto>> search(
            @Valid VendorSearchRequest searchRequest,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer page,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return CommonResponse.createSuccess(vendorService.search(searchRequest, pageRequest));
    }

    @Operation(summary = "Vendor 전체 조회", description = "페이징 없이 전체 Vendor 리스트를 조회합니다.")
    @GetMapping("/all")
    public CommonResponse<List<VendorDto>> findAll(@Valid VendorSearchRequest searchRequest) {
        return CommonResponse.createSuccess(vendorService.findAll(searchRequest));
    }

    @Operation(summary = "Vendor 생성")
    @PostMapping
    public CommonResponse<List<VendorDto>> create(@Valid @RequestBody List<VendorCreateRequest> requests) {
        List<VendorDto> dtos = requests.stream()
            .map(request -> globalMapper.map(request, VendorDto.class))
            .toList();
        return CommonResponse.createSuccess(vendorService.createList(dtos));
    }

    @Operation(summary = "Vendor 수정")
    @PutMapping("/{id}")
    public CommonResponse<VendorDto> update(
            @PathVariable Long id,
            @Valid @RequestBody VendorUpdateRequest request) {
        VendorDto dto = globalMapper.map(request, VendorDto.class);
        return CommonResponse.createSuccess(vendorService.update(id, dto));
    }

    @Operation(summary = "Vendor 일괄 수정")
    @PutMapping
    public CommonResponse<List<VendorDto>> updateAll(
            @Valid @RequestBody List<VendorUpdateAllRequest> requests) {
        List<VendorDto> dtos = requests.stream()
            .map(request -> globalMapper.map(request, VendorDto.class))
            .toList();
        return CommonResponse.createSuccess(vendorService.updateAll(dtos));
    }

    @Operation(summary = "Vendor 삭제")
    @DeleteMapping
    public CommonResponse<?> delete(@RequestBody List<Long> ids) {
        vendorService.delete(ids);
        return CommonResponse.createSuccessWithNoContent();
    }

    @Operation(summary = "Vendor 특정 필드 값 전체 조회")
    @GetMapping("/fields/{fieldName}")
    public CommonResponse<List<?>> findAllFieldValues(
            @PathVariable String fieldName,
            @Valid VendorSearchRequest searchRequest) {
        return CommonResponse.createSuccess(vendorService.getFieldValues(fieldName, searchRequest));
    }
}