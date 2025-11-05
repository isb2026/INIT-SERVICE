package com.lts5.init.service;

import com.lts5.init.entity.DatasetVersion;
import com.lts5.init.entity.Language;
import com.lts5.init.entity.TranslatedWord;
import com.lts5.init.repository.dataset.DatasetVersionRepository;
import com.lts5.init.repository.language.LanguageRepository;
import com.lts5.init.repository.translatedword.TranslatedWordRepository;
import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 번역 관련 서비스
 * - 언어별 사전 조회 기능
 * - 데이터셋 버전 조회 기능
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TranslateService {

    private final DatasetVersionRepository datasetVersionRepository;
    private final LanguageRepository languageRepository;
    private final TranslatedWordRepository translatedWordRepository;

    /**
     * 서비스1: 지정된 ISO 코드에 해당하는 언어 사전 조회
     * 
     * @param isoCode 언어 ISO 코드 (예: "ko", "en", "ja" 등)
     * @return 버전과 단어 사전이 포함된 맵
     */
    public Map<String, Object> getDictionary(String isoCode) {
        log.info("사전 조회 요청 - ISO 코드: {}", isoCode);
        
        Map<String, Object> response = new HashMap<>();
        
        // 현재 버전 조회
        Integer currentVersion = getCurrentVersion();
        response.put("version", currentVersion);
        
        // 언어 정보 조회
        Optional<Language> targetLanguage = languageRepository.findByIsoCode(isoCode);
        if (targetLanguage.isEmpty()) {
            log.warn("언어 정보를 찾을 수 없습니다. ISO 코드: {}", isoCode);
            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR);
        }
        
        // 해당 언어의 번역 단어 조회 (language 테이블 조인 없이 languageId로 직접 조회)
        List<TranslatedWord> translatedWords = translatedWordRepository.findByLanguageId(targetLanguage.get().getId());
        
        // 사전 데이터 구성: {root_word의 word: translated_word의 translatedWord}
        Map<String, String> dictionary = new HashMap<>();
        for (TranslatedWord translatedWord : translatedWords) {
            String rootWordText = translatedWord.getRootWord().getWord();
            String translatedText = translatedWord.getTranslatedWord();
            dictionary.put(rootWordText, translatedText);
        }
        
        response.put("words", dictionary);
        log.info("{} 언어 사전 조회 완료 - 총 {}개 단어", isoCode, dictionary.size());
        
        return response;
    }

    /**
     * 서비스2: 현재 데이터셋 버전 조회
     * 
     * @return 현재 버전이 포함된 맵
     */
    public Boolean checkVersion(Integer version) {
        log.info("현재 데이터셋 버전 조회 요청");
        
        Integer currentVersion = getCurrentVersion();
        
        log.info("현재 데이터셋 버전 조회 완료 - 버전: {}", currentVersion);
        
        return version.equals(currentVersion) ? true : false;
    }

    /**
     * 현재 데이터셋 버전 조회 (내부 메서드)
     * 
     * @return 현재 버전
     */
    private Integer getCurrentVersion() {
        try {
            return datasetVersionRepository.getCurrentVersion();
        } catch (Exception e) {
            log.warn("데이터셋 버전 조회 중 오류 발생, 기본값 0 반환", e);
            return 0;
        }
    }
} 