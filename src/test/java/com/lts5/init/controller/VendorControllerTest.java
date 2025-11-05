package com.lts5.init.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lts5.init.entity.ItemProgress;
import com.lts5.init.entity.Vendor;
import com.lts5.init.payload.request.vendor.VendorCreateRequest;

import com.lts5.init.payload.request.vendor.VendorUpdateRequest;
import com.lts5.init.payload.request.vendor.VendorUpdateAllRequest;
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
import org.springframework.test.web.servlet.MvcResult;
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
class VendorControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ItemProgressRepository itemProgressRepository;

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
    void 거래처_조회_유효한검색조건_성공응답반환() throws Exception {
        // given
        Vendor vendor1 = Vendor.builder()
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
        vendorRepository.save(vendor1);

        Vendor vendor2 = Vendor.builder()
                .tenantId((short) 10001)
                .compCode("V002")
                .compName("LG전자")
                .compType("대기업")
                .ceoName("이영희")
                .compEmail("contact@lg.com")
                .telNumber("02-2345-6789")
                .addressMst("서울시 영등포구")
                .addressDtl("LG로 128")
                .zipCode("07336")
                .licenseNo("110111-2345678")
                .faxNumber("02-2345-6790")
                .build();
        vendorRepository.save(vendor2);

        // when & then
        mockMvc.perform(get("/vendor")
                        .param("compName", "LG전자")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].compName").value("LG전자"))
                .andExpect(jsonPath("$.data.content[0].compCode").value("V002"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void 거래처_수정_유효한수정데이터_성공응답반환() throws Exception {
        // given
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
        
        VendorUpdateRequest updateRequest = new VendorUpdateRequest();
        updateRequest.setCompName("삼성전자 수정");
        updateRequest.setCompType("소기업");
        updateRequest.setCeoName("김철수 수정");
        updateRequest.setCompEmail("contact@samsung-updated.com");

        // when & then
        MvcResult result = mockMvc.perform(put("/vendor/{id}", savedVendor.getId())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        
        mockMvc.perform(put("/vendor/{id}", savedVendor.getId())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(savedVendor.getId()))
                .andExpect(jsonPath("$.data.compName").value("삼성전자 수정"))
                .andExpect(jsonPath("$.data.compType").value("소기업"));
    }

    @Test
    void 거래처_삭제_유효한ID_성공응답반환() throws Exception {
        // given
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
        
        // when & then
        mockMvc.perform(delete("/vendor")
                        .content(objectMapper.writeValueAsString(Arrays.asList(savedVendor.getId())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 거래처_필드값조회_유효한필드명_성공응답반환() throws Exception {
        // given
        Vendor vendor1 = Vendor.builder()
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
        vendorRepository.save(vendor1);

        Vendor vendor2 = Vendor.builder()
                .tenantId((short) 10001)
                .compCode("V002")
                .compName("LG전자")
                .compType("대기업")
                .ceoName("이영희")
                .compEmail("contact@lg.com")
                .telNumber("02-2345-6789")
                .addressMst("서울시 영등포구")
                .addressDtl("LG로 128")
                .zipCode("07336")
                .licenseNo("110111-2345678")
                .faxNumber("02-2345-6790")
                .build();
        vendorRepository.save(vendor2);

        // when & then
        mockMvc.perform(get("/vendor/fields/compName")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].value").value("삼성전자"))
                .andExpect(jsonPath("$.data[1].value").value("LG전자"));
    }

    @Test
    void 업체_일괄생성_유효한업체리스트_성공응답반환() throws Exception {
        // given
        VendorCreateRequest vendor1 = new VendorCreateRequest();
        vendor1.setCompName("현대자동차");
        vendor1.setCompType("대기업");
        vendor1.setCeoName("박현대");
        vendor1.setCompEmail("contact@hyundai.com");
        vendor1.setTelNumber("02-3456-7890");
        vendor1.setAddressMst("서울시 강남구");
        vendor1.setAddressDtl("테헤란로 231");
        vendor1.setZipCode("06142");
        vendor1.setLicenseNo("110111-3456789");
        vendor1.setFaxNumber("02-3456-7891");

        VendorCreateRequest vendor2 = new VendorCreateRequest();
        vendor2.setCompName("기아자동차");
        vendor2.setCompType("대기업");
        vendor2.setCeoName("이기아");
        vendor2.setCompEmail("contact@kia.com");
        vendor2.setTelNumber("02-4567-8901");
        vendor2.setAddressMst("서울시 강남구");
        vendor2.setAddressDtl("기아로 1");
        vendor2.setZipCode("06142");
        vendor2.setLicenseNo("110111-4567890");
        vendor2.setFaxNumber("02-4567-8902");

        List<VendorCreateRequest> createRequests = Arrays.asList(vendor1, vendor2);

        // when & then
        mockMvc.perform(post("/vendor")
                        .content(objectMapper.writeValueAsString(createRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].compName").value("현대자동차"))
                .andExpect(jsonPath("$.data[1].compName").value("기아자동차"));
    }

    @Test
    void 업체_일괄수정_유효한업체리스트_성공응답반환() throws Exception {
        // given
        Vendor vendor1 = Vendor.builder()
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
        Vendor savedVendor1 = vendorRepository.save(vendor1);

        Vendor vendor2 = Vendor.builder()
                .tenantId((short) 10001)
                .compCode("V002")
                .compName("LG전자")
                .compType("대기업")
                .ceoName("이영희")
                .compEmail("contact@lg.com")
                .telNumber("02-2345-6789")
                .addressMst("서울시 영등포구")
                .addressDtl("LG로 128")
                .zipCode("07336")
                .licenseNo("110111-2345678")
                .faxNumber("02-2345-6790")
                .build();
        Vendor savedVendor2 = vendorRepository.save(vendor2);

        VendorUpdateAllRequest updateRequest1 = new VendorUpdateAllRequest();
        updateRequest1.setId(savedVendor1.getId());
        updateRequest1.setCompName("삼성전자 수정");
        updateRequest1.setCompType("중기업");
        updateRequest1.setCeoName("김철수 수정");

        VendorUpdateAllRequest updateRequest2 = new VendorUpdateAllRequest();
        updateRequest2.setId(savedVendor2.getId());
        updateRequest2.setCompName("LG전자 수정");
        updateRequest2.setCompType("소기업");
        updateRequest2.setCeoName("이영희 수정");

        List<VendorUpdateAllRequest> updateRequests = Arrays.asList(updateRequest1, updateRequest2);

        // when & then
        mockMvc.perform(put("/vendor")
                        .content(objectMapper.writeValueAsString(updateRequests))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].compName").value("삼성전자 수정"))
                .andExpect(jsonPath("$.data[1].compName").value("LG전자 수정"));
    }

    @Test
    void 업체_일괄삭제_유효한ID리스트_성공응답반환() throws Exception {
        // given
        Vendor vendor1 = Vendor.builder()
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
        Vendor savedVendor1 = vendorRepository.save(vendor1);

        Vendor vendor2 = Vendor.builder()
                .tenantId((short) 10001)
                .compCode("V002")
                .compName("LG전자")
                .compType("대기업")
                .ceoName("이영희")
                .compEmail("contact@lg.com")
                .telNumber("02-2345-6789")
                .addressMst("서울시 영등포구")
                .addressDtl("LG로 128")
                .zipCode("07336")
                .licenseNo("110111-2345678")
                .faxNumber("02-2345-6790")
                .build();
        Vendor savedVendor2 = vendorRepository.save(vendor2);

        List<Long> ids = Arrays.asList(savedVendor1.getId(), savedVendor2.getId());

        // when & then
        mockMvc.perform(delete("/vendor")
                        .content(objectMapper.writeValueAsString(ids))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}