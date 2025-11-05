package com.lts5.init.repository.progressroute;

import com.lts5.init.entity.ProgressRoute;
import com.lts5.init.payload.request.progressroute.ProgressRouteSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProgressRouteRepositoryCustom {
    Page<ProgressRoute> search(ProgressRouteSearchRequest searchRequest, Pageable pageable);
}