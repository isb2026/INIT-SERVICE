package com.lts5.init.controller;

import com.lts5.init.service.TranslateService;
import com.primes.library.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 번역 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/word-dictionary")
@Validated
@RequiredArgsConstructor
@Tag(name = "Translation", description = "번역 사전 관리 API")
public class TranslateController {

    private final TranslateService translateService;

    /**
     * 언어별 단어 사전 조회
     * 
     * @param isoCode 언어 ISO 코드 (예: "ko", "en", "ja")
     * @return 버전과 단어 사전이 포함된 응답
     */
    @Operation(
        summary = "언어별 단어 사전 조회", 
        description = "지정된 ISO 코드에 해당하는 언어의 단어 사전을 조회합니다."
    )
    @GetMapping
    public CommonResponse<Map<String, Object>> getDictionary(
            @Parameter(description = "언어 ISO 코드", example = "ko", required = true)
            @RequestParam("iso_code") @NotBlank String isoCode) {
        
        Map<String, Object> result = translateService.getDictionary(isoCode);
        return CommonResponse.createSuccess(result);
    }

    /**
     * 현재 데이터셋 버전 조회
     * 
     * @return 현재 버전 정보
     */
    @Operation(
        summary = "데이터셋 버전 확인", 
        description = "현재 데이터셋 버전을 확인합니다."
    )
    @GetMapping("/version")
    public CommonResponse<Boolean> checkVersion(@RequestParam("version") Integer version) {
        
        Boolean result = translateService.checkVersion(version);
        return CommonResponse.createSuccess(result);
    }
} 