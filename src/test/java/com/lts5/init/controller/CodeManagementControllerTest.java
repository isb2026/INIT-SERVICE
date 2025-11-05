package com.lts5.init.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts5.init.entity.Code;
import com.lts5.init.entity.CodeGroup;
import com.lts5.init.payload.request.code.CodeCreateRequest;
import com.lts5.init.payload.request.code.CodeUpdateRequest;
import com.lts5.init.payload.request.codegroup.CodeGroupCreateRequest;
import com.lts5.init.payload.request.codegroup.CodeGroupUpdateRequest;
import com.lts5.init.repository.code.CodeRepository;
import com.lts5.init.repository.codegroup.CodeGroupRepository;
import com.primes.library.filter.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class CodeManagementControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private CodeGroupRepository codeGroupRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        // 테스트용 TenantContext 설정
        TenantContext.setTenantId((short) 10001);
    }

    @Test
    void 코드그룹_생성_유효한데이터_성공응답반환() throws Exception {
        // given
        CodeGroupCreateRequest createRequest = new CodeGroupCreateRequest();
        createRequest.setGroupCode("003");
        createRequest.setGroupName("품질 등급");
        createRequest.setDescription("품질 분류 코드");
        createRequest.setIsRoot(true);

        // when & then
        mockMvc.perform(post("/code-group")
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.groupCode").value("003"))
                .andExpect(jsonPath("$.data.groupName").value("품질 등급"))
                .andExpect(jsonPath("$.data.description").value("품질 분류 코드"));
    }

    @Test
    void 코드그룹_수정_유효한수정데이터_성공응답반환() throws Exception {
        // given
        CodeGroup codeGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("001")
                .groupName("제품 타입")
                .description("제품 분류 코드")
                .isRoot(true)
                .build();
        CodeGroup savedCodeGroup = codeGroupRepository.save(codeGroup);
        
        CodeGroupUpdateRequest updateRequest = new CodeGroupUpdateRequest();
        updateRequest.setGroupName("제품 타입 수정");
        updateRequest.setDescription("제품 분류 코드 수정");

        // when & then
        mockMvc.perform(put("/code-group/{id}", savedCodeGroup.getId())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(savedCodeGroup.getId()))
                .andExpect(jsonPath("$.data.groupName").value("제품 타입 수정"))
                .andExpect(jsonPath("$.data.description").value("제품 분류 코드 수정"));
    }

    @Test
    void 코드그룹_삭제_유효한ID_성공응답반환() throws Exception {
        // given
        CodeGroup codeGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("001")
                .groupName("제품 타입")
                .description("제품 분류 코드")
                .isRoot(true)
                .build();
        CodeGroup savedCodeGroup = codeGroupRepository.save(codeGroup);
        
        // when & then
        mockMvc.perform(delete("/code-group/{id}", savedCodeGroup.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 코드_생성_유효한데이터_성공응답반환() throws Exception {
        // given
        CodeGroup codeGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("001")
                .groupName("제품 타입")
                .description("제품 분류 코드")
                .isRoot(true)
                .build();
        CodeGroup savedCodeGroup = codeGroupRepository.save(codeGroup);

        CodeCreateRequest createRequest = new CodeCreateRequest();
        createRequest.setCodeGroupId(savedCodeGroup.getId());
        createRequest.setCodeName("노트북");
        createRequest.setDescription("노트북 제품");

        // when & then
        mockMvc.perform(post("/code")
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.codeName").value("노트북"))
                .andExpect(jsonPath("$.data.description").value("노트북 제품"))
                .andExpect(jsonPath("$.data.codeValue").value("001-001")); // codeValue가 자동 생성됨
    }

    @Test
    void 코드_수정_유효한수정데이터_성공응답반환() throws Exception {
        // given
        CodeGroup codeGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("001")
                .groupName("제품 타입")
                .description("제품 분류 코드")
                .isRoot(true)
                .build();
        CodeGroup savedCodeGroup = codeGroupRepository.save(codeGroup);

        Code code = Code.builder()
                .tenantId((short) 10001)
                .codeGroupId(savedCodeGroup.getId())
                .codeValue("001-001") // 계층형 구조에 맞게 수정
                .codeName("스마트폰")
                .description("스마트폰 제품")
                .build();
        Code savedCode = codeRepository.save(code);
        
        CodeUpdateRequest updateRequest = new CodeUpdateRequest();
        updateRequest.setCodeName("스마트폰 수정");
        updateRequest.setDescription("스마트폰 제품 수정");

        // when & then
        mockMvc.perform(put("/code/{id}", savedCode.getId())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(savedCode.getId()))
                .andExpect(jsonPath("$.data.codeName").value("스마트폰 수정"))
                .andExpect(jsonPath("$.data.description").value("스마트폰 제품 수정"));
    }

    @Test
    void 코드_삭제_유효한ID_성공응답반환() throws Exception {
        // given
        CodeGroup codeGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("001")
                .groupName("제품 타입")
                .description("제품 분류 코드")
                .isRoot(true)
                .build();
        CodeGroup savedCodeGroup = codeGroupRepository.save(codeGroup);

        Code code = Code.builder()
                .tenantId((short) 10001)
                .codeGroupId(savedCodeGroup.getId())
                .codeValue("001-001") // 계층형 구조에 맞게 수정
                .codeName("스마트폰")
                .description("스마트폰 제품")
                .build();
        Code savedCode = codeRepository.save(code);
        
        // when & then
        mockMvc.perform(delete("/code/{id}", savedCode.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 계층형코드조회_루트그룹_자식그룹리스트반환() throws Exception {
        // given
        CodeGroup rootGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("PRD")
                .groupName("제품")
                .isRoot(true)
                .description("제품 루트 그룹")
                .build();
        CodeGroup savedRootGroup = codeGroupRepository.save(rootGroup);

        CodeGroup childGroup1 = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("PRD-001") // 계층형 구조에 맞게 수정
                .groupName("제품 분류")
                .parentId(savedRootGroup.getId())
                .isRoot(false)
                .description("제품 분류 코드")
                .build();
        codeGroupRepository.save(childGroup1);

        CodeGroup childGroup2 = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("PRD-002") // 계층형 구조에 맞게 수정
                .groupName("제품 공정")
                .parentId(savedRootGroup.getId())
                .isRoot(false)
                .description("제품 공정 코드")
                .build();
        codeGroupRepository.save(childGroup2);

        // when & then
        mockMvc.perform(get("/codes/PRD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].groupCode").value("PRD-001"))
                .andExpect(jsonPath("$.data[0].groupName").value("제품 분류"))
                .andExpect(jsonPath("$.data[1].groupCode").value("PRD-002"))
                .andExpect(jsonPath("$.data[1].groupName").value("제품 공정"));
    }

    @Test
    void 계층형코드조회_자식그룹_코드리스트반환() throws Exception {
        // given
        CodeGroup rootGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("PRD")
                .groupName("제품")
                .isRoot(true)
                .description("제품 루트 그룹")
                .build();
        CodeGroup savedRootGroup = codeGroupRepository.save(rootGroup);

        CodeGroup childGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("PRD-001") // 계층형 구조에 맞게 수정
                .groupName("제품 분류")
                .parentId(savedRootGroup.getId())
                .isRoot(false)
                .description("제품 분류 코드")
                .build();
        CodeGroup savedChildGroup = codeGroupRepository.save(childGroup);

        Code code1 = Code.builder()
                .tenantId((short) 10001)
                .codeGroupId(savedChildGroup.getId())
                .codeValue("PRD-001-001") // 계층형 구조에 맞게 수정
                .codeName("스마트폰")
                .description("스마트폰 제품")
                .build();
        codeRepository.save(code1);

        Code code2 = Code.builder()
                .tenantId((short) 10001)
                .codeGroupId(savedChildGroup.getId())
                .codeValue("PRD-001-002") // 계층형 구조에 맞게 수정
                .codeName("태블릿")
                .description("태블릿 제품")
                .build();
        codeRepository.save(code2);

        // when & then
        mockMvc.perform(get("/codes/PRD-001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].codeValue").value("PRD-001-001"))
                .andExpect(jsonPath("$.data[0].codeName").value("스마트폰"))
                .andExpect(jsonPath("$.data[1].codeValue").value("PRD-001-002"))
                .andExpect(jsonPath("$.data[1].codeName").value("태블릿"));
    }

    @Test
    void 계층형코드조회_개별코드_단일코드반환() throws Exception {
        // given
        CodeGroup rootGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("PRD")
                .groupName("제품")
                .isRoot(true)
                .description("제품 루트 그룹")
                .build();
        CodeGroup savedRootGroup = codeGroupRepository.save(rootGroup);

        CodeGroup childGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("PRD-001") // 계층형 구조에 맞게 수정
                .groupName("제품 분류")
                .parentId(savedRootGroup.getId())
                .isRoot(false)
                .description("제품 분류 코드")
                .build();
        CodeGroup savedChildGroup = codeGroupRepository.save(childGroup);

        Code code = Code.builder()
                .tenantId((short) 10001)
                .codeGroupId(savedChildGroup.getId())
                .codeValue("PRD-001-001") // 계층형 구조에 맞게 수정
                .codeName("스마트폰")
                .description("스마트폰 제품")
                .build();
        codeRepository.save(code);

        // when & then
        mockMvc.perform(get("/codes/PRD-001-001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.codeValue").value("PRD-001-001"))
                .andExpect(jsonPath("$.data.codeName").value("스마트폰"))
                .andExpect(jsonPath("$.data.description").value("스마트폰 제품"));
    }

    @Test
    void 계층형코드조회_잘못된패스_오류응답반환() throws Exception {
        // when & then
        mockMvc.perform(get("/codes/PRD-001-002-003")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 전체계층형트리조회_성공응답반환() throws Exception {
        // given
        CodeGroup rootGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("PRD")
                .groupName("제품")
                .isRoot(true)
                .description("제품 루트 그룹")
                .build();
        CodeGroup savedRootGroup = codeGroupRepository.save(rootGroup);

        CodeGroup childGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("PRD-001") // 계층형 구조에 맞게 수정
                .groupName("제품 분류")
                .parentId(savedRootGroup.getId())
                .isRoot(false)
                .description("제품 분류 코드")
                .build();
        CodeGroup savedChildGroup = codeGroupRepository.save(childGroup);

        Code code = Code.builder()
                .tenantId((short) 10001)
                .codeGroupId(savedChildGroup.getId())
                .codeValue("PRD-001-001") // 계층형 구조에 맞게 수정
                .codeName("스마트폰")
                .description("스마트폰 제품")
                .build();
        codeRepository.save(code);

        // when & then
        mockMvc.perform(get("/codes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].groupCode").value("PRD"))
                .andExpect(jsonPath("$.data[0].groupName").value("제품"))
                .andExpect(jsonPath("$.data[0].children").isArray())
                .andExpect(jsonPath("$.data[0].children[0].groupCode").value("PRD-001"))
                .andExpect(jsonPath("$.data[0].children[0].groupName").value("제품 분류"))
                .andExpect(jsonPath("$.data[0].children[0].codes").isArray())
                .andExpect(jsonPath("$.data[0].children[0].codes[0].codeValue").value("PRD-001-001"))
                .andExpect(jsonPath("$.data[0].children[0].codes[0].codeName").value("스마트폰"));
    }

    @Test
    void 전체계층형코드조회_유효한전체패스_단일코드반환() throws Exception {
        // given
        CodeGroup rootGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("COM")
                .groupName("공통")
                .isRoot(true)
                .description("공통 루트 그룹")
                .build();
        CodeGroup savedRootGroup = codeGroupRepository.save(rootGroup);

        CodeGroup childGroup = CodeGroup.builder()
                .tenantId((short) 10001)
                .groupCode("COM-004") // 계층형 구조에 맞게 수정
                .groupName("공통 분류")
                .parentId(savedRootGroup.getId())
                .isRoot(false)
                .description("공통 분류 코드")
                .build();
        CodeGroup savedChildGroup = codeGroupRepository.save(childGroup);

        Code code = Code.builder()
                .tenantId((short) 10001)
                .codeGroupId(savedChildGroup.getId())
                .codeValue("COM-004-001") // 계층형 구조에 맞게 수정
                .codeName("공통코드")
                .description("공통 코드")
                .build();
        codeRepository.save(code);

        // when & then
        mockMvc.perform(get("/code/COM-004-001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.codeValue").value("COM-004-001"))
                .andExpect(jsonPath("$.data.codeName").value("공통코드"))
                .andExpect(jsonPath("$.data.description").value("공통 코드"));
    }
}