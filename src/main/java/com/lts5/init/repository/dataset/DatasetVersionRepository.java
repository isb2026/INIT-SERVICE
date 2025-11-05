package com.lts5.init.repository.dataset;

import com.lts5.init.entity.DatasetVersion;
import com.primes.library.repository.SimpleBaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface DatasetVersionRepository extends SimpleBaseRepository<DatasetVersion, Integer>, DatasetVersionRepositoryCustom {

    @Query("SELECT COALESCE(MAX(d.currentVersion), 0) FROM DatasetVersion d")
    int getCurrentVersion();

    @Transactional
    @Modifying
    @Query("UPDATE DatasetVersion d SET d.currentVersion = :version")
    int updateCurrentVersion(@Param("version") int version);

    Optional<DatasetVersion> findTopByOrderByCurrentVersionDesc();
} 