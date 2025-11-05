package com.lts5.init.repository.codegroup;

import com.lts5.init.entity.CodeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeGroupRepository extends JpaRepository<CodeGroup, Long>, CodeGroupRepositoryCustom {
    boolean existsByGroupCode(String groupCode);
    
    /**
     * 루트 그룹 중에서 특정 그룹 코드를 가진 그룹 조회
     */
    Optional<CodeGroup> findByGroupCodeAndIsRoot(String groupCode, Boolean isRoot);
    
    /**
     * 특정 부모 ID의 자식 중에서 특정 그룹 코드를 가진 그룹 조회
     */
    Optional<CodeGroup> findByGroupCodeAndParentId(String groupCode, Long parentId);

    /**
     * 특정 부모 ID의 삭제되지 않은 자식 그룹들 조회
     */
    List<CodeGroup> findByParentIdAndIsDeleteFalse(Long parentId);
    
    /**
     * 삭제되지 않은 루트 그룹들 조회
     */
    List<CodeGroup> findByIsRootAndIsDeleteFalse(Boolean isRoot);

    /**
     * groupCode로 시작하고 parentId가 null이 아닌 삭제되지 않은 그룹들 조회
     * (예: COM-001, COM-002, COM-003 등)
     */
    List<CodeGroup> findByGroupCodeStartingWithAndParentIdNotNullAndIsDeleteFalse(String groupCodePrefix);

    /**
     * groupCode로 삭제되지 않은 그룹 조회
     */
    Optional<CodeGroup> findByGroupCodeAndIsDeleteFalse(String groupCode);

    Long countSeriesByParentId(@Param("parentId") Long parentId);

    /**
     * isDelete = false 조건이 포함된 findById 메서드
     */
    Optional<CodeGroup> findByIdAndIsDeleteFalse(Long id);
}