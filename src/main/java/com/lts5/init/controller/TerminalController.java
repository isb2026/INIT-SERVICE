package com.lts5.init.controller;

import com.primes.library.common.response.CommonResponse;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.TerminalDto;
import com.lts5.init.payload.request.terminal.TerminalCreateRequest;

import com.lts5.init.payload.request.terminal.TerminalUpdateRequest;
import com.lts5.init.payload.request.terminal.TerminalUpdateAllRequest;
import com.lts5.init.payload.request.terminal.TerminalSearchRequest;
import com.lts5.init.service.TerminalService;
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
@RequestMapping("/terminal")
@Validated
@RequiredArgsConstructor
@Tag(name = "Terminal", description = "Terminal 관리 API")
public class TerminalController {
    private final TerminalService terminalService;
    private final GlobalMapper globalMapper;

    @Operation(summary = "Terminal 조회")
    @GetMapping
    public CommonResponse<Page<TerminalDto>> search(
            @Valid TerminalSearchRequest searchRequest,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer page,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return CommonResponse.createSuccess(terminalService.search(searchRequest, pageRequest));
    }

    @Operation(summary = "Terminal 전체 조회", description = "페이징 없이 전체 Terminal 리스트를 조회합니다.")
    @GetMapping("/all")
    public CommonResponse<List<TerminalDto>> findAll(@Valid TerminalSearchRequest searchRequest) {
        return CommonResponse.createSuccess(terminalService.findAll(searchRequest));
    }

    @Operation(summary = "Terminal 생성")
    @PostMapping
    public CommonResponse<List<TerminalDto>> create(@Valid @RequestBody List<TerminalCreateRequest> requests) {
        List<TerminalDto> dtos = requests.stream()
            .map(request -> globalMapper.map(request, TerminalDto.class))
            .toList();
        return CommonResponse.createSuccess(terminalService.createList(dtos));
    }

    @Operation(summary = "Terminal 수정")
    @PutMapping("/{id}")
    public CommonResponse<TerminalDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TerminalUpdateRequest request) {
        TerminalDto dto = globalMapper.map(request, TerminalDto.class);
        return CommonResponse.createSuccess(terminalService.update(id, dto));
    }

    @Operation(summary = "Terminal 일괄 수정")
    @PutMapping
    public CommonResponse<List<TerminalDto>> updateAll(
            @Valid @RequestBody List<TerminalUpdateAllRequest> requests) {
        List<TerminalDto> dtos = requests.stream()
            .map(request -> globalMapper.map(request, TerminalDto.class))
            .toList();
        return CommonResponse.createSuccess(terminalService.updateAll(dtos));
    }

    @Operation(summary = "Terminal 삭제")
    @DeleteMapping
    public CommonResponse<?> delete(@RequestBody List<Long> ids) {
        terminalService.delete(ids);
        return CommonResponse.createSuccessWithNoContent();
    }

    @Operation(summary = "Terminal 특정 필드 값 전체 조회")
    @GetMapping("/fields/{fieldName}")
    public CommonResponse<List<?>> findAllFieldValues(
            @PathVariable String fieldName,
            @Valid TerminalSearchRequest searchRequest) {
        return CommonResponse.createSuccess(terminalService.getFieldValues(fieldName, searchRequest));
    }
}