package com.lts5.init.controller;

import com.lts5.init.dto.FileLinkDto;
import com.lts5.init.payload.request.filelink.FileLinkCreateRequest;
import com.lts5.init.payload.request.filelink.FileLinkSearchRequest;
import com.lts5.init.payload.request.filelink.FileLinkUpdateAllRequest;
import com.lts5.init.payload.request.filelink.FileLinkUpdateRequest;
import com.lts5.init.service.FileLinkService;
import com.primes.library.common.response.CommonResponse;
import com.primes.library.common.mapper.GlobalMapper;
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
@RequestMapping("/filelink")
@Validated
@RequiredArgsConstructor
@Tag(name = "FileLink", description = "파일 링크 관리 API")
public class FileLinkController {
    private final FileLinkService fileLinkService;
    private final GlobalMapper globalMapper;

    @Operation(summary = "FileLink 조회")
    @GetMapping
    public CommonResponse<Page<FileLinkDto>> search(
            @Valid FileLinkSearchRequest searchRequest,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer page,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return CommonResponse.createSuccess(fileLinkService.search(searchRequest, pageRequest));
    }

    @Operation(summary = "FileLink 생성")
    @PostMapping
    public CommonResponse<List<FileLinkDto>> create(@Valid @RequestBody List<FileLinkCreateRequest> requests) {
        return CommonResponse.createSuccess(fileLinkService.createList(requests));
    }

    @Operation(summary = "FileLink 수정")
    @PutMapping("/{id}")
    public CommonResponse<FileLinkDto> update(
            @PathVariable Long id,
            @Valid @RequestBody FileLinkUpdateRequest request) {
        FileLinkDto dto = globalMapper.map(request, FileLinkDto.class);
        return CommonResponse.createSuccess(fileLinkService.update(id, dto));
    }

    @Operation(summary = "FileLink 일괄 수정")
    @PutMapping
    public CommonResponse<List<FileLinkDto>> updateAll(
            @Valid @RequestBody List<FileLinkUpdateAllRequest> requests) {
        List<FileLinkDto> dtos = requests.stream()
            .map(request -> globalMapper.map(request, FileLinkDto.class))
            .toList();
        return CommonResponse.createSuccess(fileLinkService.updateAll(dtos));
    }

    @Operation(summary = "FileLink 삭제")
    @DeleteMapping
    public CommonResponse<?> delete(@Valid @RequestBody List<Long> ids) {
        fileLinkService.delete(ids);
        return CommonResponse.createSuccessWithNoContent();
    }

    @Operation(summary = "FileLink 특정 필드 값 전체 조회")
    @GetMapping("/fields/{fieldName}")
    public CommonResponse<List<?>> findAllFieldValues(
            @PathVariable String fieldName,
            @Valid FileLinkSearchRequest searchRequest) {
        return CommonResponse.createSuccess(fileLinkService.getFieldValues(fieldName, searchRequest));
    }
}
