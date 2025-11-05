package com.lts5.init.controller;

import com.primes.library.common.response.CommonResponse;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.CodeDto;
import com.lts5.init.dto.CodeGroupDto;
import com.lts5.init.payload.request.code.CodeCreateRequest;
import com.lts5.init.payload.request.code.CodeUpdateRequest;
import com.lts5.init.payload.request.codegroup.CodeGroupCreateRequest;
import com.lts5.init.payload.request.codegroup.CodeGroupUpdateRequest;
import com.lts5.init.service.CodeManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
@Validated
@RequiredArgsConstructor
@Tag(name = "Code Management", description = "코드 및 코드 그룹 관리 API")
public class CodeManagementController {
    private final CodeManagementService codeManagementService;
    private final GlobalMapper globalMapper;

    // Code 관련 API
    @Operation(summary = "코드 생성")
    @PostMapping("/code")
    public CommonResponse<CodeDto> createCode(@Valid @RequestBody CodeCreateRequest request) {
        CodeDto dto = globalMapper.map(request, CodeDto.class);
        return CommonResponse.createSuccess(codeManagementService.createCode(dto));
    }

    @Operation(summary = "코드 수정")
    @PutMapping("/code/{id}")
    public CommonResponse<CodeDto> updateCode(
            @PathVariable Long id,
            @Valid @RequestBody CodeUpdateRequest request) {
        CodeDto dto = globalMapper.map(request, CodeDto.class);
        return CommonResponse.createSuccess(codeManagementService.updateCode(id, dto));
    }

    @Operation(summary = "코드 삭제")
    @DeleteMapping("/code/{id}")
    public CommonResponse<?> deleteCode(@PathVariable Long id) {
        codeManagementService.deleteCode(id);
        return CommonResponse.createSuccessWithNoContent();
    }

    // CodeGroup 관련 API
    @Operation(summary = "코드 그룹 생성")
    @PostMapping("/code-group")
    public CommonResponse<CodeGroupDto> createCodeGroup(@Valid @RequestBody CodeGroupCreateRequest request) {
        CodeGroupDto dto = globalMapper.map(request, CodeGroupDto.class);
        return CommonResponse.createSuccess(codeManagementService.createCodeGroup(dto));
    }

    @Operation(summary = "코드 그룹 수정")
    @PutMapping("/code-group/{id}")
    public CommonResponse<CodeGroupDto> updateCodeGroup(
            @PathVariable Long id,
            @Valid @RequestBody CodeGroupUpdateRequest request) {
        CodeGroupDto dto = globalMapper.map(request, CodeGroupDto.class);
        return CommonResponse.createSuccess(codeManagementService.updateCodeGroup(id, dto));
    }

    @Operation(summary = "코드 그룹 삭제")
    @DeleteMapping("/code-group/{id}")
    public CommonResponse<?> deleteCodeGroup(@PathVariable Long id) {
        codeManagementService.deleteCodeGroup(id);
        return CommonResponse.createSuccessWithNoContent();
    }

    @Operation(summary = "코드 조회",
               description = "계층형 코드 조회<br>" +
                           "- PRD: PRD 루트 그룹의 자식 그룹 리스트 조회<br>" +
                           "- PRD-001: PRD 그룹의 001 하위 그룹 코드 리스트 조회<br>" +
                           "- PRD-001-001: PRD 그룹의 001 하위 그룹에서 code_value가 001인 개별 코드 조회")
    @GetMapping("/codes/{hierarchyPath}")
    public CommonResponse<?> getCodesByHierarchyPath(@PathVariable String hierarchyPath) {
        return CommonResponse.createSuccess(codeManagementService.getCodesByHierarchyPath(hierarchyPath));
    }

    @Operation(summary = "전체 계층형 코드 조회",
               description = "전체 codeValue로 코드 조회 (예: COM-004-001)")
    @GetMapping("/code/{fullHierarchyPath}")
    public CommonResponse<CodeDto> getCodeByFullHierarchyPath(@PathVariable String fullHierarchyPath) {
        return CommonResponse.createSuccess(codeManagementService.getCodeByFullHierarchyPath(fullHierarchyPath));
    }

    @Operation(summary = "전체 계층형 코드 트리 조회", description = "모든 루트 그룹과 하위 그룹, 코드들을 계층형 트리 구조로 조회")
    @GetMapping("/codes")
    public CommonResponse<List<CodeGroupDto>> getAllCodesTree() {
        return CommonResponse.createSuccess(codeManagementService.getAllCodesTree());
    }
} 