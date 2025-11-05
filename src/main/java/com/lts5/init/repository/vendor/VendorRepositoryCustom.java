package com.lts5.init.repository.vendor;

import com.lts5.init.dto.VendorDto;
import com.lts5.init.payload.request.vendor.VendorSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VendorRepositoryCustom {
    Page<VendorDto> search(VendorSearchRequest searchRequest, Pageable pageable);
    List<VendorDto> searchAll(VendorSearchRequest searchRequest);
}