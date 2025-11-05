package com.lts5.init.repository.rootproduct;

import com.lts5.init.entity.RootProduct;
import com.primes.library.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RootProductRepository extends BaseRepository<RootProduct, Long> {
    
    /**
     * 아이템 ID로 루트 제품 조회 (중복 확인용)
     */
    Optional<RootProduct> findByItemIdAndIsDeleteFalse(Long itemId);
    
    /**
     * 제품 코드로 루트 제품 조회 (중복 확인용)
     */
    Optional<RootProduct> findByProductCodeAndTenantIdAndIsDeleteFalse(String productCode, Integer tenantId);
}
