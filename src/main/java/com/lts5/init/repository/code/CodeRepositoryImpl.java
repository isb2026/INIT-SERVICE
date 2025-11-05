package com.lts5.init.repository.code;

import com.lts5.init.entity.Code;
import com.lts5.init.payload.request.code.CodeSearchRequest;
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

import static com.lts5.init.entity.QCode.code;

public class CodeRepositoryImpl extends QuerydslRepositorySupport implements CodeRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    public CodeRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Code.class);
        this.queryFactory = queryFactory;
    }
    
    @Override
    public Page<Code> search(CodeSearchRequest searchRequest, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqIsUse(searchRequest.getIsUse()))
                .and(goeCreatedAtStart(searchRequest.getCreatedAtStart()))
                .and(loeCreatedAtEnd(searchRequest.getCreatedAtEnd()))
                .and(containsCreatedBy(searchRequest.getCreatedBy()))
                .and(goeUpdatedAtStart(searchRequest.getUpdatedAtStart()))
                .and(loeUpdatedAtEnd(searchRequest.getUpdatedAtEnd()))
                .and(containsUpdatedBy(searchRequest.getUpdatedBy()))
                .and(eqId(searchRequest.getId()))
                .and(eqCodeGroupId(searchRequest.getCodeGroupId()))
                .and(containsCodeValue(searchRequest.getCodeValue()))
                .and(containsCodeName(searchRequest.getCodeName()))
                .and(containsDescription(searchRequest.getDescription()))
                .and(code.isDelete.eq(false));

        List<Code> results = queryFactory
                .selectFrom(code)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(code.id.desc())
                .fetch();
                
        long total = queryFactory
                .selectFrom(code)
                .where(builder)
                .fetchCount();
                
        return new PageImpl<>(results, pageable, total);
    }

    
    private BooleanExpression eqIsUse(Boolean isUse) {
        return isUse != null ? code.isUse.eq(isUse) : null;
    }

    private BooleanExpression goeCreatedAtStart(String createdAtStart) {
        return StringUtils.hasText(createdAtStart) ? code.createdAt.goe(LocalDateTime.parse(createdAtStart)) : null;
    }

    private BooleanExpression loeCreatedAtEnd(String createdAtEnd) {
        return StringUtils.hasText(createdAtEnd) ? code.createdAt.loe(LocalDateTime.parse(createdAtEnd)) : null;
    }

    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? code.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression goeUpdatedAtStart(String updatedAtStart) {
        return StringUtils.hasText(updatedAtStart) ? code.updatedAt.goe(LocalDateTime.parse(updatedAtStart)) : null;
    }

    private BooleanExpression loeUpdatedAtEnd(String updatedAtEnd) {
        return StringUtils.hasText(updatedAtEnd) ? code.updatedAt.loe(LocalDateTime.parse(updatedAtEnd)) : null;
    }

    private BooleanExpression containsUpdatedBy(String updatedBy) {
        return StringUtils.hasText(updatedBy) ? code.updatedBy.containsIgnoreCase(updatedBy) : null;
    }

    private BooleanExpression eqId(Long id) {
        return id != null ? code.id.eq(id) : null;
    }

    private BooleanExpression eqCodeGroupId(Long codeGroupId) {
        return codeGroupId != null ? code.codeGroup.id.eq(codeGroupId) : null;
    }

    private BooleanExpression containsCodeValue(String codeValue) {
        return StringUtils.hasText(codeValue) ? code.codeValue.containsIgnoreCase(codeValue) : null;
    }

    private BooleanExpression containsCodeName(String codeName) {
        return StringUtils.hasText(codeName) ? code.codeName.containsIgnoreCase(codeName) : null;
    }

    private BooleanExpression containsDescription(String description) {
        return StringUtils.hasText(description) ? code.description.containsIgnoreCase(description) : null;
    }
}