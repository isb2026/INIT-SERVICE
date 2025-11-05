package com.lts5.init.service;

import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.EntityNotFoundException;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.VendorDto;
import com.lts5.init.entity.Vendor;
import com.lts5.init.payload.request.vendor.VendorSearchRequest;
import com.lts5.init.repository.vendor.VendorRepository;
import com.primes.library.filter.TenantContext;
import com.primes.library.util.DynamicFieldQueryUtil;
import com.primes.library.util.NumberCodeGenerator;
import com.primes.library.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VendorService extends BaseService {
    private final VendorRepository vendorRepository;
    private final DynamicFieldQueryUtil dynamicFieldQueryUtil;
    private final NumberCodeGenerator numberCodeGenerator;
    private final GlobalMapper globalMapper;

    @Transactional
    public VendorDto create(VendorDto dto) {
        Vendor entity = globalMapper.map(dto, Vendor.class);
        Vendor savedEntity = vendorRepository.save(entity);
        return globalMapper.map(savedEntity, VendorDto.class);
    }
    
    @Transactional
    public List<VendorDto> createList(List<VendorDto> dtos) {
        // 각 DTO에 compCode 자동 생성
        dtos.forEach(dto -> {
            if (dto.getCompCode() == null || dto.getCompCode().trim().isEmpty()) {
                String generatedCompCode = generateVendorCode();
                dto.setCompCode(generatedCompCode);
            }
        });
        
        List<Vendor> entities = dtos.stream()
                .map(dto -> globalMapper.map(dto, Vendor.class))
                .toList();
        
        List<Vendor> savedEntities = vendorRepository.saveAll(entities);
        return savedEntities.stream()
                .map(entity -> globalMapper.map(entity, VendorDto.class))
                .toList();
    }
    
    @Transactional
    public VendorDto update(Long id, VendorDto dto) {
        dto.setId(id);  // DTO에 ID 설정
        return updateSingle(dto);
    }
    
    @Transactional
    public List<VendorDto> updateAll(List<VendorDto> dtos) {
        return dtos.stream()
                .map(this::updateSingle)
                .toList();
    }

    @Transactional
    public void delete(List<Long> ids) {
        List<Vendor> existingEntities = vendorRepository.findAllById(ids);
        
        if (existingEntities.size() != ids.size()) {
            List<Long> existingIds = existingEntities.stream().map(Vendor::getId).toList();
            List<Long> notFoundIds = new ArrayList<>(ids);
            notFoundIds.removeAll(existingIds);

            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR,
                String.format("Id가 %s인 Vendor 데이터가 없습니다.", notFoundIds));
        }

        for (Vendor entity : existingEntities) {
            entity.delete();
        }
        vendorRepository.saveAll(existingEntities);
    }

    public List<?> getFieldValues(String fieldName, VendorSearchRequest searchRequest) {
        if (searchRequest == null) {
            return dynamicFieldQueryUtil.getFieldValues(Vendor.class, fieldName);
        }else{
            return dynamicFieldQueryUtil.getFieldValuesWithFilter(Vendor.class,  fieldName, searchRequest);
        }
    }

    public Page<VendorDto> search(VendorSearchRequest searchRequest, Pageable pageable) {
        return vendorRepository.search(searchRequest, pageable);
    }

    public List<VendorDto> findAll(VendorSearchRequest searchRequest) {
        // 페이징 없이 전체 리스트 반환
        return vendorRepository.searchAll(searchRequest);
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 테넌트 단위로 Vendor 코드 자동 생성
     */
    private String generateVendorCode() {
        Short tenantId = TenantContext.getTenantId();
        
        NumberCodeGenerator.CodeGeneratorParam<Short> param =
                NumberCodeGenerator.CodeGeneratorParam.series(
                    this::countVendorsByTenant, 
                    "three", 
                    tenantId
                );

        String nextNumber = numberCodeGenerator.generate(param);
        return "V" + nextNumber; // V001, V002, V003 형식
    }

    /**
     * 현재 테넌트의 Vendor 개수 조회
     */
    private Long countVendorsByTenant(Short tenantId) {
        return vendorRepository.countByTenantIdAndIsDeleteFalse(tenantId);
    }

    private VendorDto updateSingle(VendorDto dto) {
        Vendor entity = vendorRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, String.format("Id 가 %d인 Vendor 데이터가 없습니다.", dto.getId())));
        updateEntityFromDto(entity, dto);
        return globalMapper.map(entity, VendorDto.class);
    }
}