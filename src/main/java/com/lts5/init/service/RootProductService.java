package com.lts5.init.service;

import com.lts5.init.dto.RootProductDto;
import com.lts5.init.entity.RootProduct;
import com.lts5.init.repository.rootproduct.RootProductRepository;
import com.primes.library.filter.TenantContext;
import com.primes.library.service.BaseService;
import com.primes.library.common.mapper.GlobalMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RootProductService extends BaseService {
    
    private final RootProductRepository rootProductRepository;
    private final GlobalMapper globalMapper;

    @Transactional
    public RootProductDto create(RootProductDto dto) {
        // 1. 동일한 아이템 ID로 이미 등록된 루트 제품이 있는지 확인
        Optional<RootProduct> existingProduct = rootProductRepository.findByItemIdAndIsDeleteFalse(dto.getItemId());
        if (existingProduct.isPresent()) {
            throw new IllegalArgumentException(
                String.format("아이템 ID %d는 이미 루트 제품으로 등록되어 있습니다.", dto.getItemId()));
        }

        // 2. 제품 코드 중복 확인 (제품 코드가 있는 경우)
        if (dto.getProductCode() != null && !dto.getProductCode().trim().isEmpty()) {
            Optional<RootProduct> existingByCode = rootProductRepository
                .findByProductCodeAndTenantIdAndIsDeleteFalse(dto.getProductCode(), TenantContext.getTenantId().intValue());
            if (existingByCode.isPresent()) {
                throw new IllegalArgumentException(
                    String.format("제품 코드 '%s'는 이미 사용 중입니다.", dto.getProductCode()));
            }
        }

        // 3. 루트 제품 생성
        RootProduct entity = globalMapper.map(dto, RootProduct.class);
        RootProduct savedEntity = rootProductRepository.save(entity);
        
        // 4. 루트 제품 자기 참조 MBOM 생성 (parentItemId = null)
        // 이 부분은 MbomService에서 처리하거나 별도 로직으로 구현 가능
        
        log.info("루트 제품 생성 완료 - itemId: {}, productName: {}", dto.getItemId(), dto.getProductName());
        return globalMapper.map(savedEntity, RootProductDto.class);
    }

}
