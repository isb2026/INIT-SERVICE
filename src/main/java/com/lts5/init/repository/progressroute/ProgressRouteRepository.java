package com.lts5.init.repository.progressroute;

import com.lts5.init.entity.ProgressRoute;
import com.primes.library.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressRouteRepository extends BaseRepository<ProgressRoute, Long>, ProgressRouteRepositoryCustom {}