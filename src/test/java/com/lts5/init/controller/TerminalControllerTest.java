package com.lts5.init.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts5.init.entity.Terminal;
import com.lts5.init.payload.request.terminal.TerminalCreateRequest;

import com.lts5.init.payload.request.terminal.TerminalUpdateRequest;
import com.lts5.init.payload.request.terminal.TerminalUpdateAllRequest;
import com.lts5.init.repository.terminal.TerminalRepository;
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
class TerminalControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TerminalRepository terminalRepository;

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
    void 단말기_조회_유효한검색조건_성공응답반환() throws Exception {
        // given
        Terminal terminal1 = Terminal.builder()
                .tenantId((short) 10001)
                .accountYear((short) 2024)
                .terminalCode("TERM-001")
                .terminalName("스마트폰 단말기")
                .description("스마트폰용 단말기")
                .imageUrl("http://example.com/image1.jpg")
                .build();
        terminalRepository.save(terminal1);

        Terminal terminal2 = Terminal.builder()
                .tenantId((short) 10001)
                .accountYear((short) 2024)
                .terminalCode("TERM-002")
                .terminalName("태블릿 단말기")
                .description("태블릿용 단말기")
                .imageUrl("http://example.com/image2.jpg")
                .build();
        terminalRepository.save(terminal2);

        // when & then
        mockMvc.perform(get("/terminal")
                        .param("terminalName", "태블릿 단말기")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].terminalName").value("태블릿 단말기"))
                .andExpect(jsonPath("$.data.content[0].terminalCode").value("TERM-002"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void 단말기_수정_유효한수정데이터_성공응답반환() throws Exception {
        // given
        Terminal terminal = Terminal.builder()
                .tenantId((short) 10001)
                .accountYear((short) 2024)
                .terminalCode("TERM-001")
                .terminalName("스마트폰 단말기")
                .description("스마트폰용 단말기")
                .imageUrl("http://example.com/image1.jpg")
                .build();
        Terminal savedTerminal = terminalRepository.save(terminal);
        
        TerminalUpdateRequest updateRequest = new TerminalUpdateRequest();
        updateRequest.setTerminalName("스마트폰 단말기 수정");
        updateRequest.setDescription("스마트폰용 단말기 수정");
        updateRequest.setImageUrl("http://example.com/image1-updated.jpg");

        // when & then
        mockMvc.perform(put("/terminal/{id}", savedTerminal.getId())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(savedTerminal.getId()))
                .andExpect(jsonPath("$.data.terminalName").value("스마트폰 단말기 수정"))
                .andExpect(jsonPath("$.data.description").value("스마트폰용 단말기 수정"));
    }

    @Test
    void 단말기_삭제_유효한ID_성공응답반환() throws Exception {
        // given
        Terminal terminal = Terminal.builder()
                .tenantId((short) 10001)
                .accountYear((short) 2024)
                .terminalCode("TERM-001")
                .terminalName("스마트폰 단말기")
                .description("스마트폰용 단말기")
                .imageUrl("http://example.com/image1.jpg")
                .build();
        Terminal savedTerminal = terminalRepository.save(terminal);
        
        // when & then
        mockMvc.perform(delete("/terminal")
                        .content(objectMapper.writeValueAsString(Arrays.asList(savedTerminal.getId())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 단말기_필드값조회_유효한필드명_성공응답반환() throws Exception {
        // given
        Terminal terminal1 = Terminal.builder()
                .tenantId((short) 10001)
                .accountYear((short) 2024)
                .terminalCode("TERM-001")
                .terminalName("스마트폰 단말기")
                .description("스마트폰용 단말기")
                .imageUrl("http://example.com/image1.jpg")
                .build();
        terminalRepository.save(terminal1);

        Terminal terminal2 = Terminal.builder()
                .tenantId((short) 10001)
                .accountYear((short) 2024)
                .terminalCode("TERM-002")
                .terminalName("태블릿 단말기")
                .description("태블릿용 단말기")
                .imageUrl("http://example.com/image2.jpg")
                .build();
        terminalRepository.save(terminal2);

        // when & then
        mockMvc.perform(get("/terminal/fields/terminalName")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].value").value("스마트폰 단말기"))
                .andExpect(jsonPath("$.data[1].value").value("태블릿 단말기"));
    }

    @Test
    void 단말기_일괄생성_유효한단말기리스트_성공응답반환() throws Exception {
        // given
        TerminalCreateRequest terminal1 = new TerminalCreateRequest();
        terminal1.setTerminalName("노트북 단말기");
        terminal1.setDescription("노트북용 단말기");
        terminal1.setImageUrl("http://example.com/image3.jpg");

        TerminalCreateRequest terminal2 = new TerminalCreateRequest();
        terminal2.setTerminalName("데스크톱 단말기");
        terminal2.setDescription("데스크톱용 단말기");
        terminal2.setImageUrl("http://example.com/image4.jpg");

        List<TerminalCreateRequest> createRequests = Arrays.asList(terminal1, terminal2);

        // when & then
        mockMvc.perform(post("/terminal")
                        .content(objectMapper.writeValueAsString(createRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].terminalName").value("노트북 단말기"))
                .andExpect(jsonPath("$.data[1].terminalName").value("데스크톱 단말기"));
    }

    @Test
    void 단말기_일괄수정_유효한단말기리스트_성공응답반환() throws Exception {
        // given
        Terminal terminal1 = Terminal.builder()
                .tenantId((short) 10001)
                .accountYear((short) 2024)
                .terminalCode("TERM-001")
                .terminalName("스마트폰 단말기")
                .description("스마트폰용 단말기")
                .imageUrl("http://example.com/image1.jpg")
                .build();
        Terminal savedTerminal1 = terminalRepository.save(terminal1);

        Terminal terminal2 = Terminal.builder()
                .tenantId((short) 10001)
                .accountYear((short) 2024)
                .terminalCode("TERM-002")
                .terminalName("태블릿 단말기")
                .description("태블릿용 단말기")
                .imageUrl("http://example.com/image2.jpg")
                .build();
        Terminal savedTerminal2 = terminalRepository.save(terminal2);

        TerminalUpdateAllRequest updateRequest1 = new TerminalUpdateAllRequest();
        updateRequest1.setId(savedTerminal1.getId());
        updateRequest1.setTerminalName("스마트폰 단말기 수정");
        updateRequest1.setDescription("스마트폰용 단말기 수정");
        updateRequest1.setImageUrl("http://example.com/image1-updated.jpg");

        TerminalUpdateAllRequest updateRequest2 = new TerminalUpdateAllRequest();
        updateRequest2.setId(savedTerminal2.getId());
        updateRequest2.setTerminalName("태블릿 단말기 수정");
        updateRequest2.setDescription("태블릿용 단말기 수정");
        updateRequest2.setImageUrl("http://example.com/image2-updated.jpg");

        List<TerminalUpdateAllRequest> updateRequests = Arrays.asList(updateRequest1, updateRequest2);

        // when & then
        mockMvc.perform(put("/terminal")
                        .content(objectMapper.writeValueAsString(updateRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].terminalName").value("스마트폰 단말기 수정"))
                .andExpect(jsonPath("$.data[1].terminalName").value("태블릿 단말기 수정"));
    }

    @Test
    void 단말기_일괄삭제_유효한ID리스트_성공응답반환() throws Exception {
        // given
        Terminal terminal1 = Terminal.builder()
                .tenantId((short) 10001)
                .accountYear((short) 2024)
                .terminalCode("TERM-001")
                .terminalName("스마트폰 단말기")
                .description("스마트폰용 단말기")
                .imageUrl("http://example.com/image1.jpg")
                .build();
        Terminal savedTerminal1 = terminalRepository.save(terminal1);

        Terminal terminal2 = Terminal.builder()
                .tenantId((short) 10001)
                .accountYear((short) 2024)
                .terminalCode("TERM-002")
                .terminalName("태블릿 단말기")
                .description("태블릿용 단말기")
                .imageUrl("http://example.com/image2.jpg")
                .build();
        Terminal savedTerminal2 = terminalRepository.save(terminal2);

        List<Long> ids = Arrays.asList(savedTerminal1.getId(), savedTerminal2.getId());

        // when & then
        mockMvc.perform(delete("/terminal")
                        .content(objectMapper.writeValueAsString(ids))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}