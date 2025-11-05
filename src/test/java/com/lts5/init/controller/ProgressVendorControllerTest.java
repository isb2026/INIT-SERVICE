package com.lts5.init.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts5.init.entity.Item;
import com.lts5.init.entity.ItemProgress;
import com.lts5.init.entity.ProgressVendor;
import com.lts5.init.entity.Vendor;
import com.lts5.init.payload.request.progressvendor.ProgressVendorCreateRequest;
import com.lts5.init.payload.request.progressvendor.ProgressVendorSearchRequest;
import com.lts5.init.payload.request.progressvendor.ProgressVendorUpdateAllRequest;
import com.lts5.init.payload.request.progressvendor.ProgressVendorUpdateRequest;
import com.lts5.init.repository.item.ItemRepository;
import com.lts5.init.repository.itemprogress.ItemProgressRepository;
import com.lts5.init.repository.progressvendor.ProgressVendorRepository;
import com.lts5.init.repository.vendor.VendorRepository;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
class ProgressVendorControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProgressVendorRepository progressVendorRepository;

    @Autowired
    private ItemProgressRepository itemProgressRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ItemRepository itemRepository;

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
    void 공정업체관계_조회_유효한검색조건_성공응답반환() throws Exception {
        // given
        Item item = createTestItem();
        ItemProgress itemProgress = createTestItemProgress(item);
        Vendor vendor = createTestVendor();
        
        ProgressVendor progressVendor = ProgressVendor.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor.getId())
                .unitCost(new BigDecimal("1500.00"))
                .quantity(new BigDecimal("10.0"))
                .unit("EA")
                .isDefaultVendor(true)
                .createBy("testUser")
                .build();
        progressVendorRepository.save(progressVendor);

        // when & then
        mockMvc.perform(get("/progress-vendors")
                        .param("progressId", itemProgress.getId().toString())
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].progressId").value(itemProgress.getId()))
                .andExpect(jsonPath("$.data.content[0].vendorId").value(vendor.getId()))
                .andExpect(jsonPath("$.data.content[0].unitCost").value(1500.00))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void 공정업체관계_생성_유효한요청_성공응답반환() throws Exception {
        // given
        Item item = createTestItem();
        ItemProgress itemProgress = createTestItemProgress(item);
        Vendor vendor = createTestVendor();

        ProgressVendorCreateRequest request1 = ProgressVendorCreateRequest.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor.getId())
                .unitCost(new BigDecimal("2000.00"))
                .quantity(new BigDecimal("5.0"))
                .unit("KG")
                .isDefaultVendor(false)
                .createBy("testUser")
                .build();

        List<ProgressVendorCreateRequest> requests = Arrays.asList(request1);

        // when & then
        mockMvc.perform(post("/progress-vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].progressId").value(itemProgress.getId()))
                .andExpect(jsonPath("$.data[0].vendorId").value(vendor.getId()))
                .andExpect(jsonPath("$.data[0].unitCost").value(2000.00))
                .andExpect(jsonPath("$.data[0].quantity").value(5.0))
                .andExpect(jsonPath("$.data[0].unit").value("KG"))
                .andExpect(jsonPath("$.data[0].isDefaultVendor").value(false));
    }

    @Test
    void 공정업체관계_생성_중복된관계_실패응답반환() throws Exception {
        // given
        Item item = createTestItem();
        ItemProgress itemProgress = createTestItemProgress(item);
        Vendor vendor = createTestVendor();

        // 기존 관계 생성
        ProgressVendor existingProgressVendor = ProgressVendor.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor.getId())
                .unitCost(new BigDecimal("1000.00"))
                .quantity(new BigDecimal("1.0"))
                .unit("EA")
                .isDefaultVendor(true)
                .createBy("testUser")
                .build();
        progressVendorRepository.save(existingProgressVendor);

        ProgressVendorCreateRequest request = ProgressVendorCreateRequest.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor.getId())
                .unitCost(new BigDecimal("2000.00"))
                .quantity(new BigDecimal("2.0"))
                .unit("EA")
                .isDefaultVendor(false)
                .createBy("testUser")
                .build();

        List<ProgressVendorCreateRequest> requests = Arrays.asList(request);

        // when & then
        mockMvc.perform(post("/progress-vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void 공정업체관계_수정_유효한요청_성공응답반환() throws Exception {
        // given
        Item item = createTestItem();
        ItemProgress itemProgress = createTestItemProgress(item);
        Vendor vendor = createTestVendor();

        ProgressVendor progressVendor = ProgressVendor.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor.getId())
                .unitCost(new BigDecimal("1000.00"))
                .quantity(new BigDecimal("1.0"))
                .unit("EA")
                .isDefaultVendor(false)
                .createBy("testUser")
                .build();
        progressVendorRepository.save(progressVendor);

        ProgressVendorUpdateRequest updateRequest = ProgressVendorUpdateRequest.builder()
                .unitCost(new BigDecimal("1500.00"))
                .quantity(new BigDecimal("2.0"))
                .unit("KG")
                .isDefaultVendor(true)
                .updateBy("updateUser")
                .build();

        // when & then
        mockMvc.perform(put("/progress-vendors/{progressId}/{vendorId}", 
                        itemProgress.getId(), vendor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.progressId").value(itemProgress.getId()))
                .andExpect(jsonPath("$.data.vendorId").value(vendor.getId()))
                .andExpect(jsonPath("$.data.unitCost").value(1500.00))
                .andExpect(jsonPath("$.data.quantity").value(2.0))
                .andExpect(jsonPath("$.data.unit").value("KG"))
                .andExpect(jsonPath("$.data.isDefaultVendor").value(true));
    }

    @Test
    void 공정업체관계_일괄수정_유효한요청_성공응답반환() throws Exception {
        // given
        Item item = createTestItem();
        ItemProgress itemProgress = createTestItemProgress(item);
        Vendor vendor1 = createTestVendor();
        Vendor vendor2 = createTestVendor2();

        ProgressVendor progressVendor1 = ProgressVendor.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor1.getId())
                .unitCost(new BigDecimal("1000.00"))
                .quantity(new BigDecimal("1.0"))
                .unit("EA")
                .isDefaultVendor(false)
                .createBy("testUser")
                .build();
        progressVendorRepository.save(progressVendor1);

        ProgressVendor progressVendor2 = ProgressVendor.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor2.getId())
                .unitCost(new BigDecimal("800.00"))
                .quantity(new BigDecimal("2.0"))
                .unit("KG")
                .isDefaultVendor(false)
                .createBy("testUser")
                .build();
        progressVendorRepository.save(progressVendor2);

        ProgressVendorUpdateAllRequest updateRequest1 = ProgressVendorUpdateAllRequest.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor1.getId())
                .unitCost(new BigDecimal("1200.00"))
                .quantity(new BigDecimal("1.5"))
                .unit("EA")
                .isDefaultVendor(true)
                .updateBy("updateUser")
                .build();

        ProgressVendorUpdateAllRequest updateRequest2 = ProgressVendorUpdateAllRequest.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor2.getId())
                .unitCost(new BigDecimal("900.00"))
                .quantity(new BigDecimal("2.5"))
                .unit("KG")
                .isDefaultVendor(false)
                .updateBy("updateUser")
                .build();

        List<ProgressVendorUpdateAllRequest> updateRequests = Arrays.asList(updateRequest1, updateRequest2);

        // when & then
        mockMvc.perform(put("/progress-vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].unitCost").value(1200.00))
                .andExpect(jsonPath("$.data[1].unitCost").value(900.00));
    }

    @Test
    void 공정업체관계_삭제_유효한요청_성공응답반환() throws Exception {
        // given
        Item item = createTestItem();
        ItemProgress itemProgress = createTestItemProgress(item);
        Vendor vendor1 = createTestVendor();
        Vendor vendor2 = createTestVendor2();

        ProgressVendor progressVendor1 = ProgressVendor.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor1.getId())
                .unitCost(new BigDecimal("1000.00"))
                .quantity(new BigDecimal("1.0"))
                .unit("EA")
                .isDefaultVendor(false)
                .createBy("testUser")
                .build();
        progressVendorRepository.save(progressVendor1);

        ProgressVendor progressVendor2 = ProgressVendor.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor2.getId())
                .unitCost(new BigDecimal("800.00"))
                .quantity(new BigDecimal("2.0"))
                .unit("KG")
                .isDefaultVendor(false)
                .createBy("testUser")
                .build();
        progressVendorRepository.save(progressVendor2);

        List<Long> vendorIds = Arrays.asList(vendor1.getId(), vendor2.getId());

        // when & then
        mockMvc.perform(delete("/progress-vendors/{progressId}", itemProgress.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vendorIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void 특정공정의업체목록조회_유효한요청_성공응답반환() throws Exception {
        // given
        Item item = createTestItem();
        ItemProgress itemProgress = createTestItemProgress(item);
        Vendor vendor1 = createTestVendor();
        Vendor vendor2 = createTestVendor2();

        ProgressVendor progressVendor1 = ProgressVendor.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor1.getId())
                .unitCost(new BigDecimal("1000.00"))
                .quantity(new BigDecimal("1.0"))
                .unit("EA")
                .isDefaultVendor(true)
                .createBy("testUser")
                .build();
        progressVendorRepository.save(progressVendor1);

        ProgressVendor progressVendor2 = ProgressVendor.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor2.getId())
                .unitCost(new BigDecimal("800.00"))
                .quantity(new BigDecimal("2.0"))
                .unit("KG")
                .isDefaultVendor(false)
                .createBy("testUser")
                .build();
        progressVendorRepository.save(progressVendor2);

        // when & then
        String response = mockMvc.perform(get("/progress-vendors/item-progress/{progressId}/vendors", itemProgress.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].progressId").value(itemProgress.getId()))
                .andExpect(jsonPath("$.data[1].progressId").value(itemProgress.getId()))
                .andReturn().getResponse().getContentAsString();
        
        System.out.println("Response: " + response);
    }

    @Test
    void 특정공정의기본업체조회_유효한요청_성공응답반환() throws Exception {
        // given
        Item item = createTestItem();
        ItemProgress itemProgress = createTestItemProgress(item);
        Vendor vendor1 = createTestVendor();
        Vendor vendor2 = createTestVendor2();

        ProgressVendor progressVendor1 = ProgressVendor.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor1.getId())
                .unitCost(new BigDecimal("1000.00"))
                .quantity(new BigDecimal("1.0"))
                .unit("EA")
                .isDefaultVendor(true)
                .createBy("testUser")
                .build();
        progressVendorRepository.save(progressVendor1);

        ProgressVendor progressVendor2 = ProgressVendor.builder()
                .progressId(itemProgress.getId())
                .vendorId(vendor2.getId())
                .unitCost(new BigDecimal("800.00"))
                .quantity(new BigDecimal("2.0"))
                .unit("KG")
                .isDefaultVendor(false)
                .createBy("testUser")
                .build();
        progressVendorRepository.save(progressVendor2);

        // when & then
        mockMvc.perform(get("/progress-vendors/item-progress/{progressId}/default-vendors", itemProgress.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].progressId").value(itemProgress.getId()))
                .andExpect(jsonPath("$.data[0].vendorId").value(vendor1.getId()))
                .andExpect(jsonPath("$.data[0].isDefaultVendor").value(true));
        
    }

    // ==================== 테스트 데이터 생성 메서드 ====================

    private Item createTestItem() {
        Item item = Item.builder()
                .tenantId((short) 10001)
                .itemName("테스트 아이템")
                .itemNumber("TEST-001")
                .itemSpec("테스트 스펙")
                .itemModel("TEST-MODEL")
                .itemUnit("EA")
                .lotSize("100")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        return itemRepository.save(item);
    }

    private ItemProgress createTestItemProgress(Item item) {
        ItemProgress itemProgress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("테스트 공정")
                .isOutsourcing(true)
                .item(item)
                .build();
        return itemProgressRepository.save(itemProgress);
    }

    private Vendor createTestVendor() {
        Vendor vendor = Vendor.builder()
                .tenantId((short) 10001)
                .compCode("V001")
                .compType("001")
                .licenseNo("123-45-67890")
                .compName("테스트 업체1")
                .ceoName("김대표")
                .compEmail("test1@company.com")
                .telNumber("02-1234-5678")
                .faxNumber("02-1234-5679")
                .zipCode("12345")
                .addressDtl("상세주소1")
                .addressMst("기본주소1")
                .build();
        return vendorRepository.save(vendor);
    }

    private Vendor createTestVendor2() {
        Vendor vendor = Vendor.builder()
                .tenantId((short) 10001)
                .compCode("V002")
                .compType("002")
                .licenseNo("123-45-67891")
                .compName("테스트 업체2")
                .ceoName("이대표")
                .compEmail("test2@company.com")
                .telNumber("02-2234-5678")
                .faxNumber("02-2234-5679")
                .zipCode("23456")
                .addressDtl("상세주소2")
                .addressMst("기본주소2")
                .build();
        return vendorRepository.save(vendor);
    }
} 