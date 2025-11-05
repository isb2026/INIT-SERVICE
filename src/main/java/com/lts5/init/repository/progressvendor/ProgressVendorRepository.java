package com.lts5.init.repository.progressvendor;

import com.lts5.init.entity.ProgressVendor;
import com.lts5.init.entity.ProgressVendorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressVendorRepository extends JpaRepository<ProgressVendor, ProgressVendorId>, ProgressVendorRepositoryCustom {

    @Query("SELECT pv FROM ProgressVendor pv JOIN FETCH pv.vendor WHERE pv.progressId = :progressId ORDER BY pv.isDefaultVendor DESC, pv.vendorId ASC")
    List<ProgressVendor> findByProgressIdWithVendor(@Param("progressId") Long progressId);
    
    List<ProgressVendor> findByProgressId(Long progressId);
    
    List<ProgressVendor> findByVendorId(Long vendorId);
    
    Optional<ProgressVendor> findByProgressIdAndVendorId(Long progressId, Long vendorId);
    
    @Query("SELECT pv FROM ProgressVendor pv " +
           "JOIN FETCH pv.vendor v " +
           "WHERE pv.progressId = :progressId AND pv.isDefaultVendor = :isDefaultVendor")
    List<ProgressVendor> findByProgressIdAndIsDefaultVendorWithVendor(@Param("progressId") Long progressId, @Param("isDefaultVendor") Boolean isDefaultVendor);
    
    void deleteByProgressIdAndVendorId(Long progressId, Long vendorId);
} 