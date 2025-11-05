package com.lts5.init.controller;

import com.primes.library.common.response.CommonResponse;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.MbomDto;
import com.lts5.init.dto.MbomListDto;
import com.lts5.init.payload.request.mbom.MbomCreateRequest;

import com.lts5.init.payload.request.mbom.MbomUpdateRequest;
import com.lts5.init.payload.request.mbom.MbomUpdateAllRequest;
import com.lts5.init.dto.ProcessTreeNodeDto;
import com.lts5.init.dto.FullBomTreeDto;
import com.lts5.init.payload.request.mbom.MbomSearchRequest;
import com.lts5.init.service.mbom.MbomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mbom")
@Validated
@RequiredArgsConstructor
@Tag(name = "Mbom", description = "Mbom 관리 API")
public class MbomController {
    private final MbomService mbomService;
    private final GlobalMapper globalMapper;


    @Operation(summary = "Mbom 생성")
    @PostMapping
    public CommonResponse<List<MbomDto>> create(@Valid @RequestBody List<MbomCreateRequest> requests) {
        List<MbomDto> dtos = requests.stream()
            .map(request -> globalMapper.map(request, MbomDto.class))
            .toList();
        return CommonResponse.createSuccess(mbomService.createList(dtos));
    }

    @Operation(summary = "Mbom 수정")
    @PutMapping("/{id}")
    public CommonResponse<MbomDto> update(
            @PathVariable Long id,
            @Valid @RequestBody MbomUpdateRequest request) {
        MbomDto dto = globalMapper.map(request, MbomDto.class);
        return CommonResponse.createSuccess(mbomService.update(id, dto));
    }

    @Operation(summary = "Mbom 일괄 수정")
    @PutMapping
    public CommonResponse<List<MbomDto>> updateAll(
            @Valid @RequestBody List<MbomUpdateAllRequest> requests) {
        List<MbomDto> dtos = requests.stream()
            .map(request -> globalMapper.map(request, MbomDto.class))
            .toList();
        return CommonResponse.createSuccess(mbomService.updateAll(dtos));
    }

    @Operation(summary = "Mbom 삭제")
    @DeleteMapping
    public CommonResponse<?> delete(@RequestBody List<Long> ids) {
        mbomService.delete(ids);
        return CommonResponse.createSuccessWithNoContent();
    }



    @Operation(summary = "TreeView UI용 공정 트리 조회", 
               description = "프론트엔드 TreeView 컴포넌트에 최적화된 트리 구조로 공정과 투입품을 반환합니다.\n" +
                           "- 공정 순서대로 정렬\n" +
                           "- TreeNode 형태의 계층 구조\n" +
                           "- 투입품이 없는 공정도 포함")
    @GetMapping("/tree-ui/{itemId}")
    public CommonResponse<List<ProcessTreeNodeDto>> getProcessTreeForUI(@PathVariable Long itemId) {
        return CommonResponse.createSuccess(mbomService.getProcessTreeForUI(itemId));
    }

    @Operation(summary = "재귀적 TreeView UI용 공정 트리 조회", 
               description = "하위 제품의 공정까지 포함한 재귀적 트리 구조를 반환합니다.\n" +
                           "- maxDepth로 깊이 제한 가능 (null이면 무제한)\n" +
                           "- 투입품이 다른 제품인 경우 해당 제품의 공정도 포함")
    @GetMapping("/tree-ui/{itemId}/recursive")
    public CommonResponse<List<ProcessTreeNodeDto>> getRecursiveProcessTreeForUI(
            @PathVariable Long itemId,
            @RequestParam(required = false) Integer maxDepth) {
        return CommonResponse.createSuccess(mbomService.getRecursiveProcessTreeForUI(itemId, maxDepth));
    }


    @Operation(summary = "전체 BOM 트리 조회", 
               description = "테넌트의 모든 BOM 데이터를 트리 구조로 반환합니다.\n" +
                           "- 모든 루트 아이템들과 그 하위 트리를 포함\n" +
                           "- 프론트엔드에서 전체 BOM 구조를 한 번에 처리할 수 있음\n" +
                           "- 루트 아이템 개수와 전체 BOM 개수 정보 포함\n" +
                           "- 테넌트 ID는 인증 정보에서 자동으로 가져옴")
    @GetMapping("/full-tree")
    public CommonResponse<FullBomTreeDto> getFullBomTree() {
        return CommonResponse.createSuccess(mbomService.getFullBomTree());
    }

    @Operation(summary = "MBOM 관계 추가 가능 여부 검증", 
               description = "특정 부모-자식 관계를 추가할 때 순환 참조가 발생하는지 미리 검증합니다.\n" +
                           "- true: 추가 가능 (순환 참조 없음)\n" +
                           "- false: 추가 불가능 (순환 참조 발생)\n" +
                           "- rootItemId와 parentItemId가 같으면 Root Item 직계로 판단하여 parentItemId를 null로 처리")
    @GetMapping("/can-add-relation")
    public CommonResponse<Boolean> canAddMbomRelation(
            @RequestParam Long rootItemId,
            @RequestParam Long parentItemId, 
            @RequestParam Long childItemId) {
        
        boolean canAdd = mbomService.canAddRelation(rootItemId, parentItemId, childItemId);
        return CommonResponse.createSuccess(canAdd);
    }

    @Operation(summary = "완제품별 투입품 리스트 조회", 
               description = "완제품 ID를 받아서 모든 투입품을 depth와 함께 리스트 형태로 반환합니다.\n" +
                           "- depth 0: 완제품 자체\n" +
                           "- depth 1: 1단계 투입품\n" +
                           "- depth 2: 2단계 투입품 (투입품의 투입품)\n" +
                           "- path: 경로 정보 (예: '1.2.3')\n" +
                           "- sequence: 같은 레벨에서의 순서\n" +
                           "- hasChildren: 하위 투입품 존재 여부\n" +
                           "- maxDepth: 최대 깊이 제한 (선택사항, 기본값 무제한)")
    @GetMapping("/list/{rootItemId}")
    public CommonResponse<List<MbomListDto>> getMbomListByRootItem(
            @PathVariable Long rootItemId,
            @RequestParam(required = false) Integer maxDepth,
            @Valid MbomSearchRequest searchRequest) {
        return CommonResponse.createSuccess(mbomService.getMbomListByRootItem(rootItemId, maxDepth, searchRequest));
    }
}