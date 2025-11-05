package com.lts5.init.repository.progressvendor;

import com.lts5.init.entity.ProgressVendor;
import com.lts5.init.payload.request.progressvendor.ProgressVendorSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProgressVendorRepositoryCustom {

    Page<ProgressVendor> searchProgressVendors(ProgressVendorSearchRequest request, Pageable pageable);
} 