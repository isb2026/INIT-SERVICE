package com.lts5.init.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts5.init.entity.DatasetVersion;
import com.lts5.init.entity.Language;
import com.lts5.init.entity.RootWord;
import com.lts5.init.entity.TranslatedWord;
import com.lts5.init.repository.dataset.DatasetVersionRepository;
import com.lts5.init.repository.language.LanguageRepository;
import com.lts5.init.repository.rootword.RootWordRepository;
import com.lts5.init.repository.translatedword.TranslatedWordRepository;
import com.primes.library.filter.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
class TranslateControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DatasetVersionRepository datasetVersionRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private RootWordRepository rootWordRepository;

    @Autowired
    private TranslatedWordRepository translatedWordRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        // 테스트용 TenantContext 설정
        TenantContext.setTenantId((short) 10001);
        
        // 테스트 데이터 설정
        setupTestData();
    }

    private void setupTestData() {
        // 데이터셋 버전 설정
        DatasetVersion datasetVersion = DatasetVersion.builder()
                .currentVersion(1)
                .build();
        datasetVersionRepository.save(datasetVersion);

        // 한국어 언어 설정
        Language korean = Language.builder()
                .name("한국어")
                .isoCode("ko")
                .build();
        korean = languageRepository.save(korean);

        // 영어 언어 설정
        Language english = Language.builder()
                .name("English")
                .isoCode("en")
                .build();
        english = languageRepository.save(english);

        // 루트 단어 설정 (영어 기준)
        RootWord helloWord = RootWord.builder()
                .word("hello")
                .language(english)
                .build();
        helloWord = rootWordRepository.save(helloWord);

        RootWord worldWord = RootWord.builder()
                .word("world")
                .language(english)
                .build();
        worldWord = rootWordRepository.save(worldWord);

        // 한국어 번역 단어 설정
        TranslatedWord helloKorean = TranslatedWord.builder()
                .rootWord(helloWord)
                .language(korean)
                .translatedWord("안녕하세요")
                .build();
        translatedWordRepository.save(helloKorean);

        TranslatedWord worldKorean = TranslatedWord.builder()
                .rootWord(worldWord)
                .language(korean)
                .translatedWord("세계")
                .build();
        translatedWordRepository.save(worldKorean);
    }

    @Test
    void 언어별사전조회_유효한ISO코드_성공응답반환() throws Exception {
        // when & then
        mockMvc.perform(get("/word-dictionary")
                        .param("iso_code", "ko"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.version").value(1))
                .andExpect(jsonPath("$.data.words").exists())
                .andExpect(jsonPath("$.data.words.hello").value("안녕하세요"))
                .andExpect(jsonPath("$.data.words.world").value("세계"));
    }

    @Test
    void 버전확인_유효한데이터_성공응답반환() throws Exception {
        // when & then
        mockMvc.perform(get("/word-dictionary/version")
                        .param("version", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value("true"));
    }
}
