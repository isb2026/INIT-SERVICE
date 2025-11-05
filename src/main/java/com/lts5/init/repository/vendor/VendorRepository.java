package com.lts5.init.repository.vendor;

import com.lts5.init.entity.Vendor;
import com.primes.library.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends BaseRepository<Vendor, Long>, VendorRepositoryCustom {
    
    /**
     * 테넌트별 삭제되지 않은 Vendor 개수 조회
     */
    Long countByTenantIdAndIsDeleteFalse(Short tenantId);
}