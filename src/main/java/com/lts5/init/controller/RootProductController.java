package com.lts5.init.controller;

import com.lts5.init.dto.RootProductDto;
import com.lts5.init.payload.request.rootproduct.RootProductCreateRequest;
import com.lts5.init.service.RootProductService;
import com.primes.library.common.response.CommonResponse;
import com.primes.library.common.mapper.GlobalMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/root-product")
@Validated
@RequiredArgsConstructor
@Tag(name = "RootProduct", description = "루트 제품 관리 API")
public class RootProductController {
    
    private final RootProductService rootProductService;
    private final GlobalMapper globalMapper;

    @Operation(summary = "루트 제품 생성", 
               description = "새로운 루트 제품을 생성합니다.\n" +
                           "- 동일한 아이템 ID로는 하나의 루트 제품만 생성 가능합니다.\n" +
                           "- 제품 코드는 테넌트 내에서 유일해야 합니다.\n" +
                           "- 루트 제품은 생성 후 수정/삭제가 제한됩니다.")
    @PostMapping
    public CommonResponse<RootProductDto> create(@Valid @RequestBody RootProductCreateRequest request) {
        RootProductDto dto = globalMapper.map(request, RootProductDto.class);
        return CommonResponse.createSuccess(rootProductService.create(dto));
    }
}
