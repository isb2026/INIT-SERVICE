package com.lts5.init.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts5.init.entity.Item;
import com.lts5.init.entity.ItemProgress;
import com.lts5.init.entity.Mbom;
import com.lts5.init.repository.item.ItemRepository;
import com.lts5.init.repository.itemprogress.ItemProgressRepository;
import com.lts5.init.repository.mbom.MbomRepository;
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

import java.util.List;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lts5.init.payload.request.mbom.MbomCreateRequest;
import com.lts5.init.payload.request.mbom.MbomUpdateRequest;
import com.lts5.init.payload.request.mbom.MbomUpdateAllRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
class MbomControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MbomRepository mbomRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemProgressRepository itemProgressRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 지원
        TenantContext.setTenantId((short) 10001);
    }

    @Test
    void 비오엠_생성_유효한데이터_성공응답반환() throws Exception {
        // 필요한 Item 데이터 생성
        Item parentItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("부모 아이템")
                .itemNumber("PARENT-001")
                .itemSpec("부모 사양")
                .itemModel("PARENT-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedParentItem = itemRepository.save(parentItem);
        
        Item childItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("자식 아이템")
                .itemNumber("CHILD-001")
                .itemSpec("자식 사양")
                .itemModel("CHILD-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 2)
                .optimalInventoryQty(200.0)
                .safetyInventoryQty(100.0)
                .build();
        Item savedChildItem = itemRepository.save(childItem);

        // 필요한 ItemProgress 데이터 생성
        ItemProgress parentProgress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("부모 공정")
                .isOutsourcing(false)
                .itemId(savedParentItem.getId())
                .build();
        ItemProgress savedParentProgress = itemProgressRepository.save(parentProgress);

        ItemProgress childProgress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("자식 공정")
                .isOutsourcing(false)
                .itemId(savedChildItem.getId())
                .build();
        ItemProgress savedChildProgress = itemProgressRepository.save(childProgress);

        MbomCreateRequest request = new MbomCreateRequest();
        request.setIsRoot(false);
        request.setParentItemId(savedParentItem.getId());
        request.setItemId(savedChildItem.getId());
        request.setInputNum(101.0f);
        request.setInputUnit("EA");
        request.setParentProgressId(savedParentProgress.getId());
        request.setItemProgressId(savedChildProgress.getId());

        List<MbomCreateRequest> createRequests = List.of(request);

        mockMvc.perform(post("/mbom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").isNotEmpty())
                .andExpect(jsonPath("$.data[0].isRoot").value(false))
                .andExpect(jsonPath("$.data[0].parentItemId").value(savedParentItem.getId()))
                .andExpect(jsonPath("$.data[0].itemId").value(savedChildItem.getId()))
                .andExpect(jsonPath("$.data[0].inputNum").value(101.0))
                .andExpect(jsonPath("$.data[0].inputUnit").value("EA"))
                .andExpect(jsonPath("$.data[0].parentProgressId").value(savedParentProgress.getId()));
    }

    @Test
    void 비오엠_수정_유효한수정데이터_성공응답반환() throws Exception {
        // 필요한 Item 데이터 생성
        Item parentItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("부모 아이템")
                .itemNumber("PARENT-001")
                .itemSpec("부모 사양")
                .itemModel("PARENT-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedParentItem = itemRepository.save(parentItem);
        
        Item childItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("자식 아이템")
                .itemNumber("CHILD-001")
                .itemSpec("자식 사양")
                .itemModel("CHILD-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 2)
                .optimalInventoryQty(200.0)
                .safetyInventoryQty(100.0)
                .build();
        Item savedChildItem = itemRepository.save(childItem);

        // 필요한 ItemProgress 데이터 생성
        ItemProgress parentProgress1 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("부모 공정 1")
                .isOutsourcing(false)
                .itemId(savedParentItem.getId())
                .build();
        ItemProgress savedParentProgress1 = itemProgressRepository.save(parentProgress1);

        ItemProgress childProgress1 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("자식 공정 1")
                .isOutsourcing(false)
                .itemId(savedChildItem.getId())
                .build();
        ItemProgress savedChildProgress1 = itemProgressRepository.save(childProgress1);

        ItemProgress parentProgress2 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 2)
                .progressName("부모 공정 2")
                .isOutsourcing(false)
                .itemId(savedParentItem.getId())
                .build();
        ItemProgress savedParentProgress2 = itemProgressRepository.save(parentProgress2);

        ItemProgress childProgress2 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 2)
                .progressName("자식 공정 2")
                .isOutsourcing(false)
                .itemId(savedChildItem.getId())
                .build();
        ItemProgress savedChildProgress2 = itemProgressRepository.save(childProgress2);

        Mbom mbom = Mbom.builder()
                .tenantId((short) 10001)
                .isRoot(false)
                .parentItemId(savedParentItem.getId())
                .itemId(savedChildItem.getId())
                .inputNum(2.0f)
                .inputUnit("EA")
                .parentProgressId(savedParentProgress1.getId())
                .itemProgressId(savedChildProgress1.getId())
                .build();
        mbomRepository.save(mbom);

        MbomUpdateRequest request = new MbomUpdateRequest();
        request.setIsRoot(false);
        request.setParentItemId(savedParentItem.getId());
        request.setItemId(savedChildItem.getId());
        request.setInputNum(101.0f);
        request.setInputUnit("BOX");
        request.setParentProgressId(savedParentProgress2.getId());
        request.setItemProgressId(savedChildProgress2.getId());
        
        mockMvc.perform(put("/mbom/" + mbom.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.isRoot").value(false))
                .andExpect(jsonPath("$.data.parentItemId").value(savedParentItem.getId()))
                .andExpect(jsonPath("$.data.itemId").value(savedChildItem.getId()))
                .andExpect(jsonPath("$.data.inputNum").value(101.0))
                .andExpect(jsonPath("$.data.inputUnit").value("BOX"))
                .andExpect(jsonPath("$.data.parentProgressId").value(savedParentProgress2.getId()));
    }

    @Test
    void 비오엠_일괄수정_유효한수정데이터_성공응답반환() throws Exception {
        // 필요한 Item 데이터 생성
        Item parentItem1 = Item.builder()
                .tenantId((short) 10001)
                .itemName("부모 아이템 1")
                .itemNumber("PARENT-001")
                .itemSpec("부모 사양 1")
                .itemModel("PARENT-MODEL-1")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedParentItem1 = itemRepository.save(parentItem1);
        
        Item childItem1 = Item.builder()
                .tenantId((short) 10001)
                .itemName("자식 아이템 1")
                .itemNumber("CHILD-001")
                .itemSpec("자식 사양 1")
                .itemModel("CHILD-MODEL-1")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 2)
                .optimalInventoryQty(200.0)
                .safetyInventoryQty(100.0)
                .build();
        Item savedChildItem1 = itemRepository.save(childItem1);

        Item parentItem2 = Item.builder()
                .tenantId((short) 10001)
                .itemName("부모 아이템 2")
                .itemNumber("PARENT-002")
                .itemSpec("부모 사양 2")
                .itemModel("PARENT-MODEL-2")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 3)
                .optimalInventoryQty(300.0)
                .safetyInventoryQty(150.0)
                .build();
        Item savedParentItem2 = itemRepository.save(parentItem2);
        
        Item childItem2 = Item.builder()
                .tenantId((short) 10001)
                .itemName("자식 아이템 2")
                .itemNumber("CHILD-002")
                .itemSpec("자식 사양 2")
                .itemModel("CHILD-MODEL-2")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 4)
                .optimalInventoryQty(400.0)
                .safetyInventoryQty(200.0)
                .build();
        Item savedChildItem2 = itemRepository.save(childItem2);

        // 필요한 ItemProgress 데이터 생성
        ItemProgress parentProgress1 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("부모 공정 1")
                .isOutsourcing(false)
                .itemId(savedParentItem1.getId())
                .build();
        ItemProgress savedParentProgress1 = itemProgressRepository.save(parentProgress1);

        ItemProgress childProgress1 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("자식 공정 1")
                .isOutsourcing(false)
                .itemId(savedChildItem1.getId())
                .build();
        ItemProgress savedChildProgress1 = itemProgressRepository.save(childProgress1);

        ItemProgress parentProgress2 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("부모 공정 2")
                .isOutsourcing(false)
                .itemId(savedParentItem2.getId())
                .build();
        ItemProgress savedParentProgress2 = itemProgressRepository.save(parentProgress2);

        ItemProgress childProgress2 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("자식 공정 2")
                .isOutsourcing(false)
                .itemId(savedChildItem2.getId())
                .build();
        ItemProgress savedChildProgress2 = itemProgressRepository.save(childProgress2);

        Mbom mbom1 = Mbom.builder()
                .tenantId((short) 10001)
                .isRoot(false)
                .parentItemId(savedParentItem1.getId())
                .itemId(savedChildItem1.getId())
                .inputNum(2.0f)
                .inputUnit("EA")
                .parentProgressId(savedParentProgress1.getId())
                .itemProgressId(savedChildProgress1.getId())
                .build();
        Mbom mbom2 = Mbom.builder()
                .tenantId((short) 10001)
                .isRoot(false)
                .parentItemId(savedParentItem2.getId())
                .itemId(savedChildItem2.getId())
                .inputNum(3.0f)
                .inputUnit("BOX")
                .parentProgressId(savedParentProgress2.getId())
                .itemProgressId(savedChildProgress2.getId())
                .build();
        mbom1 = mbomRepository.save(mbom1);
        mbom2 = mbomRepository.save(mbom2);

        MbomUpdateAllRequest req1 = new MbomUpdateAllRequest();
        req1.setId(mbom1.getId());
        req1.setIsRoot(false);
        req1.setParentItemId(savedParentItem1.getId());
        req1.setItemId(savedChildItem1.getId());
        req1.setInputNum(101.0f);
        req1.setInputUnit("BOX");
        req1.setParentProgressId(savedParentProgress1.getId());
        req1.setItemProgressId(savedChildProgress1.getId());

        MbomUpdateAllRequest req2 = new MbomUpdateAllRequest();
        req2.setId(mbom2.getId());
        req2.setIsRoot(false);
        req2.setParentItemId(savedParentItem2.getId());
        req2.setItemId(savedChildItem2.getId());
        req2.setInputNum(201.0f);
        req2.setInputUnit("KG");
        req2.setParentProgressId(savedParentProgress2.getId());
        req2.setItemProgressId(savedChildProgress2.getId());

        List<MbomUpdateAllRequest> reqs = List.of(req1, req2);

        mockMvc.perform(put("/mbom")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqs)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].inputNum").value(101.0))
                .andExpect(jsonPath("$.data[0].inputUnit").value("BOX"))
                .andExpect(jsonPath("$.data[1].inputNum").value(201.0))
                .andExpect(jsonPath("$.data[1].inputUnit").value("KG"));
    }

    @Test
    void 비오엠_삭제_유효한삭제데이터_성공응답반환() throws Exception {
        // 필요한 Item 데이터 생성
        Item parentItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("부모 아이템")
                .itemNumber("PARENT-001")
                .itemSpec("부모 사양")
                .itemModel("PARENT-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedParentItem = itemRepository.save(parentItem);
        
        Item childItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("자식 아이템")
                .itemNumber("CHILD-001")
                .itemSpec("자식 사양")
                .itemModel("CHILD-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 2)
                .optimalInventoryQty(200.0)
                .safetyInventoryQty(100.0)
                .build();
        Item savedChildItem = itemRepository.save(childItem);

        // 필요한 ItemProgress 데이터 생성
        ItemProgress parentProgress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("부모 공정")
                .isOutsourcing(false)
                .itemId(savedParentItem.getId())
                .build();
        ItemProgress savedParentProgress = itemProgressRepository.save(parentProgress);

        ItemProgress childProgress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("자식 공정")
                .isOutsourcing(false)
                .itemId(savedChildItem.getId())
                .build();
        ItemProgress savedChildProgress = itemProgressRepository.save(childProgress);

        Mbom mbom = Mbom.builder()
                .tenantId((short) 10001)
                .isRoot(false)
                .parentItemId(savedParentItem.getId())
                .itemId(savedChildItem.getId())
                .inputNum(2.0f)
                .inputUnit("EA")
                .parentProgressId(savedParentProgress.getId())
                .itemProgressId(savedChildProgress.getId())
                .build();
        mbomRepository.save(mbom);
        
        List<Long> idsToDelete = List.of(mbom.getId());
        
        mockMvc.perform(delete("/mbom")
                        .content(objectMapper.writeValueAsString(idsToDelete))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void 비오엠_공정트리_조회_유효한아이템ID_성공응답반환() throws Exception {
        // given
        // 필요한 Item 데이터 생성
        Item parentItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("부모 아이템")
                .itemNumber("PARENT-001")
                .itemSpec("부모 사양")
                .itemModel("PARENT-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedParentItem = itemRepository.save(parentItem);
        
        Item childItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("자식 아이템")
                .itemNumber("CHILD-001")
                .itemSpec("자식 사양")
                .itemModel("CHILD-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 2)
                .optimalInventoryQty(200.0)
                .safetyInventoryQty(100.0)
                .build();
        Item savedChildItem = itemRepository.save(childItem);

        // 필요한 ItemProgress 데이터 생성
        ItemProgress parentProgress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("부모 공정")
                .isOutsourcing(false)
                .itemId(savedParentItem.getId())
                .build();
        ItemProgress savedParentProgress = itemProgressRepository.save(parentProgress);

        ItemProgress childProgress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("자식 공정")
                .isOutsourcing(false)
                .itemId(savedChildItem.getId())
                .build();
        ItemProgress savedChildProgress = itemProgressRepository.save(childProgress);

        Mbom mbom1 = Mbom.builder()
                .tenantId((short) 10001)
                .isRoot(false)
                .parentItemId(savedParentItem.getId())
                .itemId(savedChildItem.getId())
                .inputNum(1.0f)
                .inputUnit("EA")
                .parentProgressId(savedParentProgress.getId())
                .itemProgressId(savedChildProgress.getId())
                .build();
        mbomRepository.save(mbom1);

        // when & then
        mockMvc.perform(get("/mbom/tree-ui/{itemId}", savedParentItem.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void 비오엠_재귀공정트리_조회_유효한아이템ID_성공응답반환() throws Exception {
        // given
        // 필요한 Item 데이터 생성
        Item parentItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("부모 아이템")
                .itemNumber("PARENT-001")
                .itemSpec("부모 사양")
                .itemModel("PARENT-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedParentItem = itemRepository.save(parentItem);
        
        Item childItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("자식 아이템")
                .itemNumber("CHILD-001")
                .itemSpec("자식 사양")
                .itemModel("CHILD-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 2)
                .optimalInventoryQty(200.0)
                .safetyInventoryQty(100.0)
                .build();
        Item savedChildItem = itemRepository.save(childItem);

        // 필요한 ItemProgress 데이터 생성
        ItemProgress parentProgress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("부모 공정")
                .isOutsourcing(false)
                .itemId(savedParentItem.getId())
                .build();
        ItemProgress savedParentProgress = itemProgressRepository.save(parentProgress);

        ItemProgress childProgress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("자식 공정")
                .isOutsourcing(false)
                .itemId(savedChildItem.getId())
                .build();
        ItemProgress savedChildProgress = itemProgressRepository.save(childProgress);

        Mbom mbom = Mbom.builder()
                .tenantId((short) 10001)
                .isRoot(false)
                .parentItemId(savedParentItem.getId())
                .itemId(savedChildItem.getId())
                .inputNum(2.0f)
                .inputUnit("EA")
                .parentProgressId(savedParentProgress.getId())
                .itemProgressId(savedChildProgress.getId())
                .build();
        mbomRepository.save(mbom);
        mbomRepository.flush();
        
        // when & then
        mockMvc.perform(get("/mbom/tree-ui/{itemId}/recursive", savedParentItem.getId())
                .param("maxDepth", "3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void 비오엠_관계추가_검증_유효한데이터_성공응답반환() throws Exception {
        // given
        // 필요한 Item 데이터 생성
        Item rootItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("루트 아이템")
                .itemNumber("ROOT-001")
                .itemSpec("루트 사양")
                .itemModel("ROOT-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedRootItem = itemRepository.save(rootItem);
        
        Item parentItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("부모 아이템")
                .itemNumber("PARENT-001")
                .itemSpec("부모 사양")
                .itemModel("PARENT-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 2)
                .optimalInventoryQty(200.0)
                .safetyInventoryQty(100.0)
                .build();
        Item savedParentItem = itemRepository.save(parentItem);
        
        Item childItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("자식 아이템")
                .itemNumber("CHILD-001")
                .itemSpec("자식 사양")
                .itemModel("CHILD-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 3)
                .optimalInventoryQty(300.0)
                .safetyInventoryQty(150.0)
                .build();
        Item savedChildItem = itemRepository.save(childItem);
        
        // when & then
        mockMvc.perform(get("/mbom/can-add-relation")
                .param("rootItemId", savedRootItem.getId().toString())
                .param("parentItemId", savedParentItem.getId().toString())
                .param("childItemId", savedChildItem.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isBoolean());
    }

    @Test
    void 비오엠_리스트_조회_루트아이템ID_성공응답반환() throws Exception {
        // given
        
        // 필요한 Item 데이터 생성
        Item rootItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("루트 아이템")
                .itemNumber("ROOT-001")
                .itemSpec("루트 사양")
                .itemModel("ROOT-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedRootItem = itemRepository.save(rootItem);
        
        Item childItem = Item.builder()
                .tenantId((short) 10001)
                .itemName("자식 아이템")
                .itemNumber("CHILD-001")
                .itemSpec("자식 사양")
                .itemModel("CHILD-MODEL")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 2)
                .optimalInventoryQty(200.0)
                .safetyInventoryQty(100.0)
                .build();
        Item savedChildItem = itemRepository.save(childItem);
        
        // 필요한 ItemProgress 데이터 생성
        ItemProgress rootProgress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("루트 공정")
                .isOutsourcing(false)
                .itemId(savedRootItem.getId())
                .build();
        ItemProgress savedRootProgress = itemProgressRepository.save(rootProgress);

        ItemProgress childProgress = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("자식 공정")
                .isOutsourcing(false)
                .itemId(savedChildItem.getId())
                .build();
        ItemProgress savedChildProgress = itemProgressRepository.save(childProgress);
        
        Mbom mbom = Mbom.builder()
                .tenantId((short) 10001)
                .isRoot(false)
                .parentItemId(savedRootItem.getId())
                .itemId(savedChildItem.getId())
                .inputNum(2.0f)
                .inputUnit("EA")
                .parentProgressId(savedRootProgress.getId())
                .itemProgressId(savedChildProgress.getId())
                .build();
        mbomRepository.save(mbom);
        mbomRepository.flush();
        
        // when & then
        mockMvc.perform(get("/mbom/list/{rootItemId}", savedRootItem.getId())
                .param("maxDepth", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void 비오엠_전체트리_조회_성공응답반환() throws Exception {
        // given - 테스트 데이터 생성
        TenantContext.setTenantId((short) 10001);
        
        // 필요한 Item 데이터 생성
        Item rootItem1 = Item.builder()
                .tenantId((short) 10001)
                .itemName("루트 아이템 1")
                .itemNumber("ROOT-001")
                .itemSpec("루트 사양 1")
                .itemModel("ROOT-MODEL-1")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 1)
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedRootItem1 = itemRepository.save(rootItem1);
        
        Item childItem1 = Item.builder()
                .tenantId((short) 10001)
                .itemName("자식 아이템 1")
                .itemNumber("CHILD-001")
                .itemSpec("자식 사양 1")
                .itemModel("CHILD-MODEL-1")
                .itemUnit("EA")
                .lotSize("1")
                .itemNo((short) 2)
                .optimalInventoryQty(200.0)
                .safetyInventoryQty(100.0)
                .build();
        Item savedChildItem1 = itemRepository.save(childItem1);
        
        // 필요한 ItemProgress 데이터 생성
        ItemProgress rootProgress1 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("루트 공정 1")
                .isOutsourcing(false)
                .itemId(savedRootItem1.getId())
                .build();
        ItemProgress savedRootProgress1 = itemProgressRepository.save(rootProgress1);

        ItemProgress childProgress1 = ItemProgress.builder()
                .tenantId((short) 10001)
                .progressOrder((byte) 1)
                .progressName("자식 공정 1")
                .isOutsourcing(false)
                .itemId(savedChildItem1.getId())
                .build();
        ItemProgress savedChildProgress1 = itemProgressRepository.save(childProgress1);
        
        // 루트 아이템 1
        Mbom rootMbom1 = Mbom.builder()
                .tenantId((short) 10001)
                .isDelete(false)
                .isRoot(true)
                .parentItemId(null) // 루트 아이템은 parentItemId가 null
                .itemId(savedRootItem1.getId())
                .build();
        mbomRepository.save(rootMbom1);
        
        // 루트 아이템 1의 자식
        Mbom childMbom1 = Mbom.builder()
                .tenantId((short) 10001)
                .isDelete(false)
                .isRoot(false)
                .parentItemId(savedRootItem1.getId())
                .itemId(savedChildItem1.getId())
                .inputNum(2.0f)
                .inputUnit("EA")
                .parentProgressId(savedRootProgress1.getId())
                .itemProgressId(savedChildProgress1.getId())
                .build();
        mbomRepository.save(childMbom1);
        
        mbomRepository.flush();

        // when & then
        mockMvc.perform(get("/mbom/full-tree")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.rootItems").isArray())
                .andExpect(jsonPath("$.data.rootItemCount").exists())
                .andExpect(jsonPath("$.data.totalCount").exists());
    }
}