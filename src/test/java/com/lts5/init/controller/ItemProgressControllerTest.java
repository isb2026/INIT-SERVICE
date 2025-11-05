package com.lts5.init.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts5.init.entity.Item;
import com.lts5.init.entity.ItemProgress;
import com.lts5.init.entity.Vendor;
import com.lts5.init.payload.request.itemprogress.ItemProgressCreateRequest;

import com.lts5.init.payload.request.itemprogress.ItemProgressUpdateRequest;
import com.lts5.init.payload.request.itemprogress.ItemProgressUpdateAllRequest;
import com.lts5.init.payload.request.itemprogress.ItemProgressSearchRequest;
import com.lts5.init.repository.item.ItemRepository;
import com.lts5.init.repository.itemprogress.ItemProgressRepository;
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
class ItemProgressControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ItemProgressRepository itemProgressRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private VendorRepository vendorRepository;

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
    void 공정_조회_유효한검색조건_성공응답반환() throws Exception {
        // given
        Item item = Item.builder()
                .tenantId((short) 10001)
                .itemName("스마트폰")
                .itemNumber("ITEM-001")
                .itemSpec("6.1인치/128GB")
                .itemModel("SM-A123")
                .itemUnit("EA")
                .lotSize("100")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedItem = itemRepository.save(item);

        ItemProgress progress1 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("조립 공정")
                .isOutsourcing(false)
                .itemId(savedItem.getId())
                .build();
        itemProgressRepository.save(progress1);

        ItemProgress progress2 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 2)
                .progressName("검사 공정")
                .isOutsourcing(true)
                .itemId(savedItem.getId())
                .build();
        itemProgressRepository.save(progress2);

        // when & then
        mockMvc.perform(get("/item-progress")
                        .param("progressName", "조립 공정")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].progressName").value("조립 공정"))
                .andExpect(jsonPath("$.data.content[0].progressOrder").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void 공정_수정_유효한수정데이터_성공응답반환() throws Exception {
        // given
        Item item = Item.builder()
                .tenantId((short) 10001)
                .itemName("스마트폰")
                .itemNumber("ITEM-001")
                .itemSpec("6.1인치/128GB")
                .itemModel("SM-A123")
                .itemUnit("EA")
                .lotSize("100")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedItem = itemRepository.save(item);

        ItemProgress progress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("조립 공정")
                .isOutsourcing(false)
                .itemId(savedItem.getId())
                .build();
        ItemProgress savedProgress = itemProgressRepository.save(progress);
        itemProgressRepository.flush();
        ItemProgressUpdateRequest updateRequest = new ItemProgressUpdateRequest();
        updateRequest.setItemId(savedItem.getId());
        updateRequest.setProgressName("조립 공정 수정");
        updateRequest.setIsOutsourcing(true);

        // when & then
        mockMvc.perform(put("/item-progress/{id}", savedProgress.getId())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(savedProgress.getId()))
                .andExpect(jsonPath("$.data.progressName").value("조립 공정 수정"))
                .andExpect(jsonPath("$.data.isOutsourcing").value(true));
    }

    @Test
    void 공정_필드값조회_유효한필드명_성공응답반환() throws Exception {
        // given
        Item item = Item.builder()
                .tenantId((short) 10001)
                .itemName("스마트폰")
                .itemNumber("ITEM-001")
                .itemSpec("6.1인치/128GB")
                .itemModel("SM-A123")
                .itemUnit("EA")
                .lotSize("100")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedItem = itemRepository.save(item);
        itemRepository.flush();

        ItemProgress progress1 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("조립 공정")
                .isOutsourcing(false)
                .itemId(savedItem.getId())
                .build();
        itemProgressRepository.save(progress1);
        itemRepository.flush();

        ItemProgress progress2 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 2)
                .progressName("검사 공정")
                .isOutsourcing(true)
                .itemId(savedItem.getId())
                .build();
        itemProgressRepository.save(progress2);
        itemRepository.flush();

        // when & then
        mockMvc.perform(get("/item-progress/fields/progressName")
                        .param("itemId", savedItem.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data[0].value").value("조립 공정"))
                .andExpect(jsonPath("$.data[1].value").value("검사 공정"));
    }

    @Test
    void 공정_일괄생성_유효한공정리스트_성공응답반환() throws Exception {
        // given
        Item item = Item.builder()
                .tenantId((short) 10001)
                .itemName("스마트폰")
                .itemNumber("ITEM-001")
                .itemSpec("6.1인치/128GB")
                .itemModel("SM-A123")
                .itemUnit("EA")
                .lotSize("100")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedItem = itemRepository.save(item);
        itemRepository.flush();
        Vendor vendor = Vendor.builder()
                .tenantId((short) 10001)
                .compCode("V001")
                .compName("삼성전자")
                .compType("대기업")
                .ceoName("김철수")
                .compEmail("contact@samsung.com")
                .telNumber("02-1234-5678")
                .addressMst("서울시 강남구")
                .addressDtl("삼성로 129")
                .zipCode("06164")
                .licenseNo("110111-1234567")
                .faxNumber("02-1234-5679")
                .build();
        Vendor savedVendor = vendorRepository.save(vendor);
        vendorRepository.flush();
        ItemProgressCreateRequest progress1 = new ItemProgressCreateRequest();
        progress1.setItemId(savedItem.getId());
        progress1.setProgressOrder((byte) 1);
        progress1.setProgressName("포장 공정");
        progress1.setIsOutsourcing(false);

        ItemProgressCreateRequest progress2 = new ItemProgressCreateRequest();
        progress2.setItemId(savedItem.getId());
        progress2.setProgressOrder((byte) 2);
        progress2.setProgressName("출하 공정");
        progress2.setIsOutsourcing(true);

        List<ItemProgressCreateRequest> createRequests = Arrays.asList(progress1, progress2);

        // when & then
        mockMvc.perform(post("/item-progress")
                        .content(objectMapper.writeValueAsString(createRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].progressName").value("포장 공정"))
                .andExpect(jsonPath("$.data[1].progressName").value("출하 공정"));
    }

    @Test
    void 공정_일괄수정_유효한공정리스트_성공응답반환() throws Exception {
        // given
        Item item = Item.builder()
                .tenantId((short) 10001)
                .itemName("스마트폰")
                .itemNumber("ITEM-001")
                .itemSpec("6.1인치/128GB")
                .itemModel("SM-A123")
                .itemUnit("EA")
                .lotSize("100")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedItem = itemRepository.save(item);

        ItemProgress progress1 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("조립 공정")
                .isOutsourcing(false)
                .itemId(savedItem.getId())
                .build();
        ItemProgress savedProgress1 = itemProgressRepository.save(progress1);

        ItemProgress progress2 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 2)
                .progressName("검사 공정")
                .isOutsourcing(true)
                .itemId(savedItem.getId())
                .build();
        ItemProgress savedProgress2 = itemProgressRepository.save(progress2);

        ItemProgressUpdateAllRequest updateRequest1 = new ItemProgressUpdateAllRequest();
        updateRequest1.setId(savedProgress1.getId());
        updateRequest1.setItemId(savedItem.getId());
        updateRequest1.setProgressName("조립 공정 수정");
        updateRequest1.setIsOutsourcing(true);

        ItemProgressUpdateAllRequest updateRequest2 = new ItemProgressUpdateAllRequest();
        updateRequest2.setId(savedProgress2.getId());
        updateRequest2.setItemId(savedItem.getId());
        updateRequest2.setProgressName("검사 공정 수정");
        updateRequest2.setIsOutsourcing(false);

        List<ItemProgressUpdateAllRequest> updateRequests = Arrays.asList(updateRequest1, updateRequest2);

        // when & then
        mockMvc.perform(put("/item-progress")
                        .content(objectMapper.writeValueAsString(updateRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].progressName").value("조립 공정 수정"))
                .andExpect(jsonPath("$.data[1].progressName").value("검사 공정 수정"));
    }

    @Test
    void 공정_일괄삭제_유효한ID리스트_성공응답반환() throws Exception {
        // given
        Item item = Item.builder()
                .tenantId((short) 10001)
                .itemName("스마트폰")
                .itemNumber("ITEM-001")
                .itemSpec("6.1인치/128GB")
                .itemModel("SM-A123")
                .itemUnit("EA")
                .lotSize("100")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedItem = itemRepository.save(item);

        ItemProgress progress1 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("조립 공정")
                .isOutsourcing(false)
                .itemId(savedItem.getId())
                .build();
        ItemProgress savedProgress1 = itemProgressRepository.save(progress1);

        ItemProgress progress2 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 2)
                .progressName("검사 공정")
                .isOutsourcing(true)
                .itemId(savedItem.getId())
                .build();
        ItemProgress savedProgress2 = itemProgressRepository.save(progress2);

        List<Long> ids = Arrays.asList(savedProgress1.getId(), savedProgress2.getId());

        // when & then
        mockMvc.perform(delete("/item-progress")
                        .content(objectMapper.writeValueAsString(ids))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}