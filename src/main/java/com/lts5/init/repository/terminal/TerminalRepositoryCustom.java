package com.lts5.init.repository.terminal;

import com.lts5.init.entity.Terminal;
import com.lts5.init.payload.request.terminal.TerminalSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TerminalRepositoryCustom {
    Page<Terminal> search(TerminalSearchRequest searchRequest, Pageable pageable);
    List<Terminal> searchAll(TerminalSearchRequest searchRequest);
}