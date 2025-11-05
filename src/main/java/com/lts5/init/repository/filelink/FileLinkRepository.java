package com.lts5.init.repository.filelink;

import com.lts5.init.entity.FileLink;
import com.lts5.init.entity.enums.OwnerType;
import com.primes.library.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileLinkRepository extends BaseRepository<FileLink, Long>, FileLinkRepositoryCustom {

    // ownerTable과 ownerId로 FileLink 목록을 조회
    @Query("SELECT f FROM FileLink f " +
            "WHERE f.ownerTable = :ownerTable " +
            "AND f.ownerId = :ownerId " +
            "AND f.isDelete = false " +
            "ORDER BY f.sortOrder")
    List<FileLink> findByOwnerTableAndOwnerId(@Param("ownerTable") String ownerTable, @Param("ownerId") Long ownerId);

    // ownerTable, ownerType, ownerId로 FileLink 목록을 조회
    @Query("SELECT f FROM FileLink f " +
            "WHERE f.ownerTable = :ownerTable " +
            "AND f.ownerType = :ownerType " +
            "AND f.ownerId = :ownerId " +
            "AND f.isDelete = false " +
            "ORDER BY f.sortOrder")
    List<FileLink> findByOwnerTableAndOwnerTypeAndOwnerId(@Param("ownerTable") String ownerTable, @Param("ownerType") OwnerType ownerType, @Param("ownerId") Long ownerId);
}