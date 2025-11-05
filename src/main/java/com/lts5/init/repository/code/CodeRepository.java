package com.lts5.init.repository.code;

import com.lts5.init.entity.Code;
import com.lts5.init.entity.CodeGroup;
import com.primes.library.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends BaseRepository<Code, Long>, CodeRepositoryCustom {
    
    /**
     * 특정 코드 그룹에 속한 삭제되지 않은 코드들 조회
     */
    List<Code> findByCodeGroupAndIsDeleteFalse(CodeGroup codeGroup);
    
    /**
     * 특정 코드 그룹에서 특정 코드 값을 가진 삭제되지 않은 코드 조회
     */
    Optional<Code> findByCodeGroupAndCodeValueAndIsDeleteFalse(CodeGroup codeGroup, String codeValue);
    
    /**
     * 코드 값으로 삭제되지 않은 코드 조회 (코드 그룹 무관)
     */
    Optional<Code> findByCodeValueAndIsDeleteFalse(String codeValue);

    @Query("SELECT COUNT(c) FROM Code c WHERE c.codeGroup.id = :codeGroupId")
    Long countSeriesByCodeGroup(@Param("codeGroupId") Long codeGroupId);
}