package com.lts5.init.repository.item;

import com.lts5.init.payload.request.item.ItemSearchRequest;
import com.lts5.init.payload.response.item.ItemSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {
    Page<ItemSearchResponse> search(ItemSearchRequest searchRequest, Pageable pageable);
    List<ItemSearchResponse> searchAll(ItemSearchRequest searchRequest);
}