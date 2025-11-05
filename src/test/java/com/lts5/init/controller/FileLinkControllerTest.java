package com.lts5.init.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts5.init.entity.FileLink;
import com.lts5.init.entity.Item;
import com.lts5.init.entity.enums.OwnerType;
import com.lts5.init.payload.request.filelink.FileLinkCreateRequest;
import com.lts5.init.payload.request.filelink.FileLinkUpdateAllRequest;
import com.lts5.init.payload.request.filelink.FileLinkUpdateRequest;
import com.lts5.init.repository.filelink.FileLinkRepository;
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
class FileLinkControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FileLinkRepository fileLinkRepository;

    @Autowired
    private ItemRepository itemRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Long testItemId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        // 테스트용 TenantContext 설정
        TenantContext.setTenantId((short) 10001);

        // 테스트용 Item 생성
        Item testItem = Item.builder()
                .itemNo((short) 1)
                .itemNumber("TEST-001")
                .itemName("테스트 아이템")
                .itemSpec("테스트 스펙")
                .itemModel("TEST-MODEL")
                .itemType1("TYPE1")
                .itemType2("TYPE2")
                .itemType3("TYPE3")
                .itemUnit("EA")
                .lotSize("100")
                .optimalInventoryQty(100.0)
                .safetyInventoryQty(50.0)
                .build();
        Item savedItem = itemRepository.save(testItem);
        testItemId = savedItem.getId();
    }

    @Test
    void 파일_조회_유효한검색조건_성공응답반환() throws Exception {
        FileLink fileLink1 = FileLink.builder()
                .ownerTable("items")
                .ownerType(OwnerType.ITEM_IMG)
                .ownerId(testItemId)
                .url("https://example.com/test.jpg")
                .isPrimary(true)
                .sortOrder((short) 1)
                .description("테스트 이미지")
                .build();
        fileLinkRepository.save(fileLink1);
        fileLinkRepository.flush();

        FileLink fileLink2 = FileLink.builder()
                .ownerTable("items")
                .ownerType(OwnerType.ITEM_DESIGN)
                .ownerId(testItemId)
                .isPrimary(false)
                .sortOrder((short) 2)
                .url("https://example.com/test2.jpg")
                .description("테스트 이미지2")
                .build();
        fileLinkRepository.save(fileLink2);
        fileLinkRepository.flush();

        // When & Then
        mockMvc.perform(get("/filelink")
                        .param("ownerType", "ITEM_IMG")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isNotEmpty())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].sortOrder").value(1))
                .andExpect(jsonPath("$.data.content[0].url").value("https://example.com/test.jpg"));
    }

    @Test
    void 파일_일괄생성_유효한파일리스트_성공응답반환() throws Exception {
        // Given
        FileLinkCreateRequest fileLink1 = new FileLinkCreateRequest();
        fileLink1.setOwnerTable("items");
        fileLink1.setOwnerType(OwnerType.ITEM_IMG);
        fileLink1.setOwnerId(testItemId);
        fileLink1.setUrl("https://storage.com/file.jpg");
        fileLink1.setSortOrder((short) 1);
        fileLink1.setIsPrimary(true);
        fileLink1.setDescription("제품 이미지");

        FileLinkCreateRequest fileLink2 = new FileLinkCreateRequest();
        fileLink2.setOwnerTable("items");
        fileLink2.setOwnerType(OwnerType.ITEM_IMG);
        fileLink2.setOwnerId(testItemId);
        fileLink2.setUrl("https://storage.com/file2.jpg");
        fileLink2.setSortOrder((short) 2);
        fileLink2.setIsPrimary(false);
        fileLink2.setDescription("제품 이미지2");

        List<FileLinkCreateRequest> createRequests = Arrays.asList(fileLink1, fileLink2);

        // When & Then
        mockMvc.perform(post("/filelink")
                        .content(objectMapper.writeValueAsString(createRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].ownerType").value("ITEM_IMG"))
                .andExpect(jsonPath("$.data[1].description").value("제품 이미지2"));
    }

    @Test
    void 파일_수정_유효한파일리스트_성공응답반환() throws Exception {
        // Given
        FileLink fileLink1 = FileLink.builder()
                .ownerTable("items")
                .ownerType(OwnerType.ITEM_IMG)
                .ownerId(testItemId)
                .url("https://example.com/test.jpg")
                .isPrimary(true)
                .sortOrder((short) 1)
                .description("테스트 이미지")
                .build();
        FileLink savedFileLink = fileLinkRepository.save(fileLink1);

        FileLinkUpdateRequest updateRequest = new FileLinkUpdateRequest();
        updateRequest.setOwnerType(OwnerType.ITEM_DESIGN);
        updateRequest.setDescription("이미지");

        // When & Then
        mockMvc.perform(put("/filelink/{id}", savedFileLink.getId())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(savedFileLink.getId()))
                .andExpect(jsonPath("$.data.ownerType").value("ITEM_DESIGN"))
                .andExpect(jsonPath("$.data.description").value("이미지"));
    }

    @Test
    void 파일_일괄수정_유효한파일리스트_성공응답반환() throws Exception {
        // Given
        FileLink fileLink1 = FileLink.builder()
                .ownerTable("items")
                .ownerType(OwnerType.ITEM_IMG)
                .ownerId(testItemId)
                .url("https://example.com/test.jpg")
                .isPrimary(true)
                .sortOrder((short) 1)
                .description("테스트 이미지")
                .build();
        fileLinkRepository.save(fileLink1);

        FileLink fileLink2 = FileLink.builder()
                .ownerTable("items")
                .ownerType(OwnerType.ITEM_IMG)
                .ownerId(testItemId)
                .isPrimary(false)
                .sortOrder((short) 2)
                .url("https://example.com/test2.jpg")
                .description("테스트 이미지2")
                .build();
        fileLinkRepository.save(fileLink2);

        FileLinkUpdateAllRequest updateRequest1 = new FileLinkUpdateAllRequest();
        updateRequest1.setId(fileLink1.getId());
        updateRequest1.setOwnerType(OwnerType.ITEM_DESIGN);
        updateRequest1.setIsPrimary(false);
        updateRequest1.setSortOrder((short) 2);
        updateRequest1.setDescription("테스트 도면");

        FileLinkUpdateAllRequest updateRequest2 = new FileLinkUpdateAllRequest();
        updateRequest2.setId(fileLink2.getId());
        updateRequest2.setOwnerType(OwnerType.ITEM_DESIGN);
        updateRequest2.setIsPrimary(true);
        updateRequest2.setSortOrder((short) 1);
        updateRequest2.setDescription("테스트 도면2");

        List<FileLinkUpdateAllRequest> updateRequests = Arrays.asList(updateRequest1, updateRequest2);

        // When & Then
        mockMvc.perform(put("/filelink")
                        .content(objectMapper.writeValueAsString(updateRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].description").value("테스트 도면"))
                .andExpect(jsonPath("$.data[1].description").value("테스트 도면2"));
    }

    @Test
    void 파일_삭제_유효한파일ID_성공응답반환() throws Exception {
        // Given
        FileLink fileLink1 = FileLink.builder()
                .tenantId((short)10001)
                .ownerTable("items")
                .ownerType(OwnerType.ITEM_IMG)
                .ownerId(testItemId)
                .url("https://example.com/test.jpg")
                .isPrimary(true)
                .sortOrder((short) 1)
                .description("테스트 이미지")
                .build();
        FileLink savedfileLink1 = fileLinkRepository.save(fileLink1);

        FileLink fileLink2 = FileLink.builder()
                .tenantId((short)10001)
                .ownerTable("items")
                .ownerType(OwnerType.ITEM_IMG)
                .ownerId(testItemId)
                .isPrimary(false)
                .sortOrder((short) 2)
                .url("https://example.com/test2.jpg")
                .description("테스트 이미지2")
                .build();
        FileLink savedfileLink2 = fileLinkRepository.save(fileLink2);

        List<Long> ids = Arrays.asList(savedfileLink1.getId(), savedfileLink2.getId());

        // when & then
        mockMvc.perform(delete("/filelink")
                        .content(objectMapper.writeValueAsString(ids))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 파일_필드값조회_유효한필드명_성공응답반환() throws Exception {
        // Given
        FileLink fileLink1 = FileLink.builder()
                .ownerTable("items")
                .ownerType(OwnerType.ITEM_IMG)
                .ownerId(testItemId)
                .url("https://example.com/test.jpg")
                .isPrimary(true)
                .sortOrder((short) 1)
                .description("테스트 이미지")
                .build();
        fileLinkRepository.save(fileLink1);
        fileLinkRepository.flush();

        FileLink fileLink2 = FileLink.builder()
                .ownerTable("items")
                .ownerType(OwnerType.ITEM_IMG)
                .ownerId(testItemId)
                .isPrimary(false)
                .sortOrder((short) 2)
                .url("https://example.com/test2.jpg")
                .description("테스트 이미지2")
                .build();
        fileLinkRepository.save(fileLink2);
        fileLinkRepository.flush();

        FileLink fileLink3 = FileLink.builder()
                .ownerTable("items")
                .ownerType(OwnerType.ITEM_DESIGN)
                .ownerId(testItemId)
                .isPrimary(true)
                .sortOrder((short) 1)
                .url("https://example.com/design.jpg")
                .description("테스트 도면")
                .build();
        fileLinkRepository.save(fileLink3);
        fileLinkRepository.flush();

        // When & Then
        mockMvc.perform(get("/filelink/fields/url")
                        .param("ownerType", "ITEM_IMG")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data[0].value").value("https://example.com/test.jpg"))
                .andExpect(jsonPath("$.data[1].value").value("https://example.com/test2.jpg"));
    }
}
