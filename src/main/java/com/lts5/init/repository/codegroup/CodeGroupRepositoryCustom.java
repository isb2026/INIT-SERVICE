package com.lts5.init.repository.codegroup;

import com.lts5.init.entity.CodeGroup;
import com.lts5.init.payload.request.codegroup.CodeGroupSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CodeGroupRepositoryCustom {
    Page<CodeGroup> search(CodeGroupSearchRequest searchRequest, Pageable pageable);
}