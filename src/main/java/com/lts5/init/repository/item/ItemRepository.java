package com.lts5.init.repository.item;

import com.lts5.init.entity.Item;
import com.primes.library.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends BaseRepository<Item, Long>, ItemRepositoryCustom {

    @Query("SELECT COUNT(i) FROM Item i")
    Long countSeries();

    @Query("SELECT DISTINCT i FROM Item i " +
           "LEFT JOIN FETCH i.fileLinks fl " +
           "WHERE i.id = :id AND i.isDelete = false")
    Optional<Item> findByIdWithFileLinks(@Param("id") Long id);
}