package com.lts5.init.repository.terminal;

import com.lts5.init.entity.Terminal;
import com.primes.library.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminalRepository extends BaseRepository<Terminal, Long>, TerminalRepositoryCustom {
    
    /**
     * 테넌트별 삭제되지 않은 Terminal 개수 조회
     */
    Long countByTenantIdAndIsDeleteFalse(Short tenantId);
}