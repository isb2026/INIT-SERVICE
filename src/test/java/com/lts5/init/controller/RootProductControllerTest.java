package com.lts5.init.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts5.init.entity.Item;
import com.lts5.init.entity.RootProduct;
import com.lts5.init.payload.request.rootproduct.RootProductCreateRequest;
import com.lts5.init.repository.item.ItemRepository;
import com.lts5.init.repository.rootproduct.RootProductRepository;
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
class RootProductControllerTest {

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
    void 루트제품_생성_유효한데이터_성공응답반환() throws Exception {
        // given
        Item item = Item.builder()
                .tenantId((short) 10001)
                .itemNo((short) 1)
                .itemNumber("ITEM001")
                .itemName("테스트 아이템")
                .itemType1("PRODUCT")
                .build();
        Item savedItem = itemRepository.save(item);

        RootProductCreateRequest createRequest = new RootProductCreateRequest();
        createRequest.setItemId(savedItem.getId());
        createRequest.setProductCode("PROD001");
        createRequest.setProductName("테스트 루트 제품");
        createRequest.setDescription("테스트용 루트 제품");

        // when & then
        mockMvc.perform(post("/root-product")
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.productCode").value("PROD001"))
                .andExpect(jsonPath("$.data.productName").value("테스트 루트 제품"))
                .andExpect(jsonPath("$.data.description").value("테스트용 루트 제품"))
                .andExpect(jsonPath("$.data.itemId").value(savedItem.getId()));
    }
}
