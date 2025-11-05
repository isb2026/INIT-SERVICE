package com.lts5.init.repository.itemprogress;

import com.lts5.init.entity.ItemProgress;
import com.primes.library.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemProgressRepository extends BaseRepository<ItemProgress, Long>, ItemProgressRepositoryCustom {
    // 공정 순서 감소: from 초과 to 이하인 공정들의 순서를 1 감소
    @Modifying
    @Query("UPDATE ItemProgress ip SET ip.progressOrder = ip.progressOrder - 1 " +
            "WHERE ip.item.id = :itemId " +
            "AND ip.progressOrder > :from " +
            "AND ip.progressOrder <= :to")
    void decrementProgressOrders(@Param("itemId") Long itemId,
                                 @Param("from") byte from,
                                 @Param("to") byte to);

    // 공정 순서 증가: from 이상 to 미만인 공정들의 순서를 1 증가
    @Modifying
    @Query("UPDATE ItemProgress ip SET ip.progressOrder = ip.progressOrder + 1 " +
            "WHERE ip.item.id = :itemId " +
            "AND ip.progressOrder >= :from " +
            "AND ip.progressOrder < :to")
    void incrementProgressOrders(@Param("itemId") Long itemId,
                                 @Param("from") byte from,
                                 @Param("to") byte to);

    @Query("SELECT COALESCE(COUNT(ip), 0) FROM ItemProgress ip " +
            "WHERE ip.item.id = :itemId AND ip.isDelete = false")
    byte findMaxProgressOrder(@Param("itemId") Long itemId);
    
    /**
     * 아이템 ID로 공정들을 순서대로 조회
     */
    @Query("SELECT ip FROM ItemProgress ip WHERE ip.item.id = :itemId AND ip.isDelete = false ORDER BY ip.progressOrder")
    List<ItemProgress> findByItemIdOrderByProgressOrder(@Param("itemId") Long itemId);
}