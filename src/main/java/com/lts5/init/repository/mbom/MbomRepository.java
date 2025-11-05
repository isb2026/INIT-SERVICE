package com.lts5.init.repository.mbom;

import com.lts5.init.entity.Mbom;
import com.primes.library.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MbomRepository extends BaseRepository<Mbom, Long>, MbomRepositoryCustom {
    
    /**
     * 부모 아이템 ID로 MBOM 조회 (공정 ID 순 정렬)
     */
    @Query("SELECT m FROM Mbom m WHERE m.parentItemId = :parentItemId ORDER BY m.parentProgressId")
    List<Mbom> findByParentItemIdOrderByParentProgressId(@Param("parentItemId") Long parentItemId);
    
    /**
     * 특정 아이템을 자식으로 하는 MBOM들 조회 (순환 참조 검증용)
     */
    @Query("SELECT m FROM Mbom m WHERE m.itemId = :itemId")
    List<Mbom> findByItemId(@Param("itemId") Long itemId);
    
    /**
     * 특정 아이템을 부모로 하는 MBOM들 조회 (순환 참조 검증용)
     */
    @Query("SELECT m FROM Mbom m WHERE m.parentItemId = :parentItemId")
    List<Mbom> findByParentItemId(@Param("parentItemId") Long parentItemId);
    
    
    /**
     * 루트 아이템들 조회 (parentItemId가 null인 아이템들)
     */
    List<Mbom> findByParentItemIdIsNull();
    
    /**
     * MBOM 개수 조회
     */
    @Query("SELECT COUNT(m) FROM Mbom m")
    Long countAllMboms();
    
    /**
     * 특정 아이템이 투입품으로 사용되는 MBOM들 조회 (엄격한 제약 검증용)
     */
    @Query("SELECT m FROM Mbom m WHERE m.itemId = :itemId AND m.parentItemId IS NOT NULL")
    List<Mbom> findByItemIdAndParentItemIdIsNotNull(@Param("itemId") Long itemId);
    
    /**
     * 특정 아이템이 루트로 등록된 MBOM들 조회 (엄격한 제약 검증용)
     */
    @Query("SELECT m FROM Mbom m WHERE m.itemId = :itemId AND m.parentItemId IS NULL")
    List<Mbom> findByItemIdAndParentItemIdIsNull(@Param("itemId") Long itemId);
}