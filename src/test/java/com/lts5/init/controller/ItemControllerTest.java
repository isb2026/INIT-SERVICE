package com.lts5.init.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts5.init.entity.Item;
import com.lts5.init.payload.request.item.ItemCreateRequest;

import com.lts5.init.payload.request.item.ItemUpdateRequest;
import com.lts5.init.payload.request.item.ItemUpdateAllRequest;
import com.lts5.init.repository.item.ItemRepository;
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
class ItemControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

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
    void 품목_조회_유효한검색조건_성공응답반환() throws Exception {
        // given
        Item item1 = Item.builder()
                .tenantId((short)10001)
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
        itemRepository.save(item1);
        itemRepository.flush();

        Item item2 = Item.builder()
                .tenantId((short)10001)
                .itemName("태블릿")
                .itemNumber("ITEM-002")
                .itemSpec("10.1인치/256GB")
                .itemModel("TB-B456")
                .itemUnit("EA")
                .lotSize("50")
                .itemNo((short) 2)
                .optimalInventoryQty(80.0)
                .safetyInventoryQty(30.0)
                .build();
        itemRepository.save(item2);
        itemRepository.flush();

        // when & then
        mockMvc.perform(get("/item")
                        .param("itemName", "스마트폰")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].itemName").value("스마트폰"))
                .andExpect(jsonPath("$.data.content[0].itemNumber").value("ITEM-001"))
                .andExpect(jsonPath("$.data.content[0].itemSpec").value("6.1인치/128GB"));
    }

    @Test
    void 품목_수정_유효한수정데이터_성공응답반환() throws Exception {
        // given
        Item item = Item.builder()
                .tenantId((short)10001)
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
        
        ItemUpdateRequest updateRequest = new ItemUpdateRequest();
        updateRequest.setItemName("스마트폰 수정");
        updateRequest.setItemSpec("6.1인치/256GB");
        updateRequest.setOptimalInventoryQty(120.0);
        updateRequest.setSafetyInventoryQty(60.0);

        // when & then
        mockMvc.perform(put("/item/{id}", savedItem.getId())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(savedItem.getId()))
                .andExpect(jsonPath("$.data.itemName").value("스마트폰 수정"))
                .andExpect(jsonPath("$.data.itemSpec").value("6.1인치/256GB"));
    }

    @Test
    void 품목_삭제_유효한품목ID_성공응답반환() throws Exception {
        // given
        Item item = Item.builder()
                .tenantId((short)10001)
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
        
        List<Long> ids = Arrays.asList(savedItem.getId());
        
        // when & then
        mockMvc.perform(delete("/item")
                        .content(objectMapper.writeValueAsString(ids))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 품목_필드값조회_유효한필드명_성공응답반환() throws Exception {
        // given
        Item item1 = Item.builder()
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
                .isUse(true)
                .isDelete(false)
                .build();
        itemRepository.save(item1);
        itemRepository.flush();

        Item item2 = Item.builder()
                .tenantId((short) 10001)
                .itemName("노트북")
                .itemNumber("ITEM-002")
                .itemSpec("10.1인치/256GB")
                .itemModel("TB-B456")
                .itemUnit("EA")
                .lotSize("50")
                .itemNo((short) 2)
                .optimalInventoryQty(80.0)
                .safetyInventoryQty(30.0)
                .isUse(true)
                .isDelete(false)
                .build();
        itemRepository.save(item2);
        itemRepository.flush();

        Item item3 = Item.builder()
                .tenantId((short) 10001)
                .itemName("노트북")
                .itemNumber("ITEM-003")
                .itemSpec("15.6인치/512GB")
                .itemModel("NB-C789")
                .itemUnit("EA")
                .lotSize("50")
                .itemNo((short) 2)
                .optimalInventoryQty(80.0)
                .safetyInventoryQty(30.0)
                .isUse(true)
                .isDelete(false)
                .build();
        itemRepository.save(item3);
        itemRepository.flush();

        // when & then
        mockMvc.perform(get("/item/fields/itemModel")
                        .param("itemName", "노트북")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data[0].value").value("TB-B456"))
                .andExpect(jsonPath("$.data[1].value").value("NB-C789"));
    }

    @Test
    void 품목_일괄생성_유효한품목리스트_성공응답반환() throws Exception {
        // given
        ItemCreateRequest item1 = new ItemCreateRequest();
        item1.setItemName("노트북");
        item1.setItemNumber("ITEM-003");
        item1.setItemSpec("15.6인치/512GB");
        item1.setItemModel("NB-C789");
        item1.setItemUnit("EA");
        item1.setLotSizeCode("20");
        item1.setOptimalInventoryQty(60.0);
        item1.setSafetyInventoryQty(20.0);

        ItemCreateRequest item2 = new ItemCreateRequest();
        item2.setItemName("모니터");
        item2.setItemNumber("ITEM-004");
        item2.setItemSpec("27인치/4K");
        item2.setItemModel("MN-D012");
        item2.setItemUnit("EA");
        item2.setLotSizeCode("10");
        item2.setOptimalInventoryQty(40.0);
        item2.setSafetyInventoryQty(15.0);

        List<ItemCreateRequest> createRequests = Arrays.asList(item1, item2);

        // when & then
        mockMvc.perform(post("/item")
                        .content(objectMapper.writeValueAsString(createRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].itemName").value("노트북"))
                .andExpect(jsonPath("$.data[1].itemName").value("모니터"));
    }

    @Test
    void 품목_일괄수정_유효한품목리스트_성공응답반환() throws Exception {
        // given
        Item item1 = Item.builder()
                .tenantId((short)10001)
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
        Item savedItem1 = itemRepository.save(item1);

        Item item2 = Item.builder()
                .tenantId((short)10001)
                .itemName("태블릿")
                .itemNumber("ITEM-002")
                .itemSpec("10.1인치/256GB")
                .itemModel("TB-B456")
                .itemUnit("EA")
                .lotSize("50")
                .itemNo((short) 2)
                .optimalInventoryQty(80.0)
                .safetyInventoryQty(30.0)
                .build();
        Item savedItem2 = itemRepository.save(item2);

        ItemUpdateAllRequest updateRequest1 = new ItemUpdateAllRequest();
        updateRequest1.setId(savedItem1.getId());
        updateRequest1.setItemName("스마트폰 수정");
        updateRequest1.setItemSpec("6.1인치/256GB");
        updateRequest1.setOptimalInventoryQty(120.0);
        updateRequest1.setSafetyInventoryQty(60.0);

        ItemUpdateAllRequest updateRequest2 = new ItemUpdateAllRequest();
        updateRequest2.setId(savedItem2.getId());
        updateRequest2.setItemName("태블릿 수정");
        updateRequest2.setItemSpec("10.1인치/512GB");
        updateRequest2.setOptimalInventoryQty(100.0);
        updateRequest2.setSafetyInventoryQty(40.0);

        List<ItemUpdateAllRequest> updateRequests = Arrays.asList(updateRequest1, updateRequest2);

        // when & then
        mockMvc.perform(put("/item")
                        .content(objectMapper.writeValueAsString(updateRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].itemName").value("스마트폰 수정"))
                .andExpect(jsonPath("$.data[1].itemName").value("태블릿 수정"));
    }

    @Test
    void 품목_일괄삭제_유효한ID리스트_성공응답반환() throws Exception {
        // given
        Item item1 = Item.builder()
                .tenantId((short)10001)
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
        Item savedItem1 = itemRepository.save(item1);

        Item item2 = Item.builder()
                .tenantId((short)10001)
                .itemName("태블릿")
                .itemNumber("ITEM-002")
                .itemSpec("10.1인치/256GB")
                .itemModel("TB-B456")
                .itemUnit("EA")
                .lotSize("50")
                .itemNo((short) 2)
                .optimalInventoryQty(80.0)
                .safetyInventoryQty(30.0)
                .build();
        Item savedItem2 = itemRepository.save(item2);

        List<Long> ids = Arrays.asList(savedItem1.getId(), savedItem2.getId());

        // when & then
        mockMvc.perform(delete("/item")
                        .content(objectMapper.writeValueAsString(ids))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}