package com.lts5.init.repository.codegroup;

import com.lts5.init.entity.CodeGroup;
import com.lts5.init.payload.request.codegroup.CodeGroupSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.lts5.init.entity.QCodeGroup.codeGroup;

public class CodeGroupRepositoryImpl extends QuerydslRepositorySupport implements CodeGroupRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    public CodeGroupRepositoryImpl(JPAQueryFactory queryFactory) {
        super(CodeGroup.class);
        this.queryFactory = queryFactory;
    }
    
    @Override
    public Page<CodeGroup> search(CodeGroupSearchRequest searchRequest, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqIsUse(searchRequest.getIsUse()))
                .and(goeCreatedAtStart(searchRequest.getCreatedAtStart()))
                .and(loeCreatedAtEnd(searchRequest.getCreatedAtEnd()))
                .and(containsCreatedBy(searchRequest.getCreatedBy()))
                .and(goeUpdatedAtStart(searchRequest.getUpdatedAtStart()))
                .and(loeUpdatedAtEnd(searchRequest.getUpdatedAtEnd()))
                .and(containsUpdatedBy(searchRequest.getUpdatedBy()))
                .and(eqId(searchRequest.getId()))
                .and(containsGroupCode(searchRequest.getGroupCode()))
                .and(containsGroupName(searchRequest.getGroupName()))
                .and(containsDescription(searchRequest.getDescription()))
                .and(codeGroup.isDelete.eq(false));

        List<CodeGroup> results = queryFactory
                .selectFrom(codeGroup)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(codeGroup.id.desc())
                .fetch();
                
        long total = queryFactory
                .selectFrom(codeGroup)
                .where(builder)
                .fetchCount();
                
        return new PageImpl<>(results, pageable, total);
    }

    
    private BooleanExpression eqIsUse(Boolean isUse) {
        return isUse != null ? codeGroup.isUse.eq(isUse) : null;
    }

    private BooleanExpression goeCreatedAtStart(String createdAtStart) {
        return StringUtils.hasText(createdAtStart) ? codeGroup.createdAt.goe(LocalDateTime.parse(createdAtStart)) : null;
    }

    private BooleanExpression loeCreatedAtEnd(String createdAtEnd) {
        return StringUtils.hasText(createdAtEnd) ? codeGroup.createdAt.loe(LocalDateTime.parse(createdAtEnd)) : null;
    }

    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? codeGroup.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression goeUpdatedAtStart(String updatedAtStart) {
        return StringUtils.hasText(updatedAtStart) ? codeGroup.updatedAt.goe(LocalDateTime.parse(updatedAtStart)) : null;
    }

    private BooleanExpression loeUpdatedAtEnd(String updatedAtEnd) {
        return StringUtils.hasText(updatedAtEnd) ? codeGroup.updatedAt.loe(LocalDateTime.parse(updatedAtEnd)) : null;
    }

    private BooleanExpression containsUpdatedBy(String updatedBy) {
        return StringUtils.hasText(updatedBy) ? codeGroup.updatedBy.containsIgnoreCase(updatedBy) : null;
    }

    private BooleanExpression eqId(Long id) {
        return id != null ? codeGroup.id.eq(id) : null;
    }

    private BooleanExpression containsGroupCode(String groupCode) {
        return StringUtils.hasText(groupCode) ? codeGroup.groupCode.containsIgnoreCase(groupCode) : null;
    }

    private BooleanExpression containsGroupName(String groupName) {
        return StringUtils.hasText(groupName) ? codeGroup.groupName.containsIgnoreCase(groupName) : null;
    }

    private BooleanExpression containsDescription(String description) {
        return StringUtils.hasText(description) ? codeGroup.description.containsIgnoreCase(description) : null;
    }
}