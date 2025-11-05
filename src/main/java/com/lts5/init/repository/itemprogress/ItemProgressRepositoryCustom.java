package com.lts5.init.repository.itemprogress;

import com.lts5.init.entity.ItemProgress;
import com.lts5.init.payload.request.itemprogress.ItemProgressSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemProgressRepositoryCustom {
    Page<ItemProgress> search(ItemProgressSearchRequest searchRequest, Pageable pageable);
}