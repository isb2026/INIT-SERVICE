package com.lts5.init.repository.mbom;

import com.lts5.init.entity.Mbom;
import com.lts5.init.payload.request.mbom.MbomSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MbomRepositoryCustom {
    Page<Mbom> search(MbomSearchRequest searchRequest, Pageable pageable);
}