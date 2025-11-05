package com.lts5.init.repository.code;

import com.lts5.init.entity.Code;
import com.lts5.init.payload.request.code.CodeSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CodeRepositoryCustom {
    Page<Code> search(CodeSearchRequest searchRequest, Pageable pageable);
}