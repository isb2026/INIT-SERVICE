package com.lts5.init.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts5.init.entity.ProgressRoute;
import com.lts5.init.payload.request.progressroute.ProgressRouteCreateRequest;

import com.lts5.init.payload.request.progressroute.ProgressRouteUpdateRequest;
import com.lts5.init.payload.request.progressroute.ProgressRouteUpdateAllRequest;
import com.lts5.init.repository.progressroute.ProgressRouteRepository;
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

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
class ProgressRouteControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProgressRouteRepository progressRouteRepository;

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
    void 진행경로_조회_유효한검색조건_성공응답반환() throws Exception {
        // given
        ProgressRoute progressRoute1 = ProgressRoute.builder()
                .tenantId((short)10001)
                .progressSequence((byte) 1)
                .progressTypeCode("001")
                .progressTypeName("조립공정")
                .progressRealName("조립")
                .defaultCycleTime(10.5)
                .lotSize(100.0)
                .lotUnit("EA")
                .optimalProgressInventoryQty(50.0)
                .safetyProgressInventoryQty(20.0)
                .progressDefaultSpec("표준규격")
                .keyManagementContents("품질관리중요")
                .build();
        progressRouteRepository.save(progressRoute1);
        progressRouteRepository.flush();

        ProgressRoute progressRoute2 = ProgressRoute.builder()
                .tenantId((short)10001)
                .progressSequence((byte) 2)
                .progressTypeCode("002")
                .progressTypeName("검사공정")
                .progressRealName("검사")
                .defaultCycleTime(5.0)
                .lotSize(50.0)
                .lotUnit("EA")
                .optimalProgressInventoryQty(25.0)
                .safetyProgressInventoryQty(10.0)
                .progressDefaultSpec("검사규격")
                .keyManagementContents("검사중요")
                .build();
        progressRouteRepository.save(progressRoute2);
        progressRouteRepository.flush();

        // when & then
        mockMvc.perform(get("/progress-route")
                        .param("progressTypeCode", "001")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].progressTypeCode").value("001"))
                .andExpect(jsonPath("$.data.content[0].progressTypeName").value("조립공정"))
                .andExpect(jsonPath("$.data.content[0].progressRealName").value("조립"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void 진행경로_생성_유효한진행경로데이터_성공응답반환() throws Exception {
        // given
        ProgressRouteCreateRequest createRequest = new ProgressRouteCreateRequest();
        createRequest.setProgressSequence((byte) 3);
        createRequest.setProgressTypeCode("003");
        createRequest.setProgressTypeName("포장공정");
        createRequest.setProgressRealName("포장");
        createRequest.setDefaultCycleTime(3.0);
        createRequest.setLotSize(30.0);
        createRequest.setLotUnit("EA");
        createRequest.setOptimalProgressInventoryQty(15.0);
        createRequest.setSafetyProgressInventoryQty(5.0);
        createRequest.setProgressDefaultSpec("포장규격");
        createRequest.setKeyManagementContents("포장중요");

        List<ProgressRouteCreateRequest> createRequests = Arrays.asList(createRequest);

        // when & then
        mockMvc.perform(post("/progress-route")
                        .content(objectMapper.writeValueAsString(createRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].progressTypeCode").value("003"))
                .andExpect(jsonPath("$.data[0].progressTypeName").value("포장공정"))
                .andExpect(jsonPath("$.data[0].progressRealName").value("포장"));
    }

    @Test
    void 진행경로_수정_유효한수정데이터_성공응답반환() throws Exception {
        // given
        ProgressRoute progressRoute = ProgressRoute.builder()
                .tenantId((short)10001)
                .progressSequence((byte) 1)
                .progressTypeCode("001")
                .progressTypeName("조립공정")
                .progressRealName("조립")
                .defaultCycleTime(10.5)
                .lotSize(100.0)
                .lotUnit("EA")
                .optimalProgressInventoryQty(50.0)
                .safetyProgressInventoryQty(20.0)
                .progressDefaultSpec("표준규격")
                .keyManagementContents("품질관리중요")
                .build();
        ProgressRoute savedProgressRoute = progressRouteRepository.save(progressRoute);
        
        ProgressRouteUpdateRequest updateRequest = new ProgressRouteUpdateRequest();
        updateRequest.setProgressTypeName("조립공정 수정");
        updateRequest.setProgressRealName("조립 수정");
        updateRequest.setDefaultCycleTime(12.0);
        updateRequest.setOptimalProgressInventoryQty(60.0);
        updateRequest.setSafetyProgressInventoryQty(25.0);

        // when & then
        mockMvc.perform(put("/progress-route/{id}", savedProgressRoute.getId())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(savedProgressRoute.getId()))
                .andExpect(jsonPath("$.data.progressTypeName").value("조립공정 수정"))
                .andExpect(jsonPath("$.data.progressRealName").value("조립 수정"));
    }

    @Test
    void 진행경로_삭제_유효한진행경로ID_성공응답반환() throws Exception {
        // given
        ProgressRoute progressRoute = ProgressRoute.builder()
                .tenantId((short)10001)
                .progressSequence((byte) 1)
                .progressTypeCode("001")
                .progressTypeName("조립공정")
                .progressRealName("조립")
                .defaultCycleTime(10.5)
                .lotSize(100.0)
                .lotUnit("EA")
                .optimalProgressInventoryQty(50.0)
                .safetyProgressInventoryQty(20.0)
                .progressDefaultSpec("표준규격")
                .keyManagementContents("품질관리중요")
                .build();
        ProgressRoute savedProgressRoute = progressRouteRepository.save(progressRoute);
        
        // when & then
        mockMvc.perform(delete("/progress-route")
                        .content(objectMapper.writeValueAsString(Arrays.asList(savedProgressRoute.getId())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 진행경로_필드값조회_유효한필드명_성공응답반환() throws Exception {
        // given
        ProgressRoute progressRoute1 = ProgressRoute.builder()
                .tenantId((short)10001)
                .progressSequence((byte) 1)
                .progressTypeCode("001")
                .progressTypeName("조립공정")
                .progressRealName("조립")
                .defaultCycleTime(10.5)
                .lotSize(100.0)
                .lotUnit("EA")
                .optimalProgressInventoryQty(50.0)
                .safetyProgressInventoryQty(20.0)
                .progressDefaultSpec("표준규격")
                .keyManagementContents("품질관리중요")
                .build();
        progressRouteRepository.save(progressRoute1);

        ProgressRoute progressRoute2 = ProgressRoute.builder()
                .tenantId((short)10001)
                .progressSequence((byte) 2)
                .progressTypeCode("002")
                .progressTypeName("검사공정")
                .progressRealName("검사")
                .defaultCycleTime(5.0)
                .lotSize(50.0)
                .lotUnit("EA")
                .optimalProgressInventoryQty(25.0)
                .safetyProgressInventoryQty(10.0)
                .progressDefaultSpec("검사규격")
                .keyManagementContents("검사중요")
                .build();
        progressRouteRepository.save(progressRoute2);

        // when & then
        mockMvc.perform(get("/progress-route/fields/progressTypeName")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data[0].value").value("조립공정"))
                .andExpect(jsonPath("$.data[1].value").value("검사공정"));
    }

    @Test
    void 진행경로_일괄수정_유효한수정데이터_성공응답반환() throws Exception {
        // given
        ProgressRoute progressRoute1 = ProgressRoute.builder()
                .tenantId((short)10001)
                .progressSequence((byte) 1)
                .progressTypeCode("001")
                .progressTypeName("조립공정")
                .progressRealName("조립")
                .defaultCycleTime(10.5)
                .lotSize(100.0)
                .lotUnit("EA")
                .optimalProgressInventoryQty(50.0)
                .safetyProgressInventoryQty(20.0)
                .progressDefaultSpec("표준규격")
                .keyManagementContents("품질관리중요")
                .build();
        ProgressRoute savedProgressRoute1 = progressRouteRepository.save(progressRoute1);

        ProgressRoute progressRoute2 = ProgressRoute.builder()
                .tenantId((short)10001)
                .progressSequence((byte) 2)
                .progressTypeCode("002")
                .progressTypeName("검사공정")
                .progressRealName("검사")
                .defaultCycleTime(5.0)
                .lotSize(50.0)
                .lotUnit("EA")
                .optimalProgressInventoryQty(25.0)
                .safetyProgressInventoryQty(10.0)
                .progressDefaultSpec("검사규격")
                .keyManagementContents("검사중요")
                .build();
        ProgressRoute savedProgressRoute2 = progressRouteRepository.save(progressRoute2);

        ProgressRouteUpdateAllRequest updateRequest1 = new ProgressRouteUpdateAllRequest();
        updateRequest1.setId(savedProgressRoute1.getId());
        updateRequest1.setProgressTypeName("조립공정 일괄수정");
        updateRequest1.setProgressRealName("조립 일괄수정");
        updateRequest1.setDefaultCycleTime(15.0);
        updateRequest1.setOptimalProgressInventoryQty(70.0);
        updateRequest1.setSafetyProgressInventoryQty(30.0);

        ProgressRouteUpdateAllRequest updateRequest2 = new ProgressRouteUpdateAllRequest();
        updateRequest2.setId(savedProgressRoute2.getId());
        updateRequest2.setProgressTypeName("검사공정 일괄수정");
        updateRequest2.setProgressRealName("검사 일괄수정");
        updateRequest2.setDefaultCycleTime(8.0);
        updateRequest2.setOptimalProgressInventoryQty(35.0);
        updateRequest2.setSafetyProgressInventoryQty(15.0);

        List<ProgressRouteUpdateAllRequest> updateRequests = Arrays.asList(updateRequest1, updateRequest2);

        // when & then
        mockMvc.perform(put("/progress-route")
                        .content(objectMapper.writeValueAsString(updateRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(savedProgressRoute1.getId()))
                .andExpect(jsonPath("$.data[0].progressTypeName").value("조립공정 일괄수정"))
                .andExpect(jsonPath("$.data[0].progressRealName").value("조립 일괄수정"))
                .andExpect(jsonPath("$.data[1].id").value(savedProgressRoute2.getId()))
                .andExpect(jsonPath("$.data[1].progressTypeName").value("검사공정 일괄수정"))
                .andExpect(jsonPath("$.data[1].progressRealName").value("검사 일괄수정"));
    }
}