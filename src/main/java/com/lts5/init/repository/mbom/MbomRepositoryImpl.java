package com.lts5.init.repository.mbom;

import com.lts5.init.entity.Mbom;
import com.lts5.init.payload.request.mbom.MbomSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.lts5.init.entity.QMbom.mbom;


public class MbomRepositoryImpl extends QuerydslRepositorySupport implements MbomRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    public MbomRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Mbom.class);
        this.queryFactory = queryFactory;
    }
    
    @Override
    public Page<Mbom> search(MbomSearchRequest searchRequest, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(goeCreatedAtStart(searchRequest.getCreatedAtStart()))
                .and(loeCreatedAtEnd(searchRequest.getCreatedAtEnd()))
                .and(containsCreatedBy(searchRequest.getCreatedBy()))
                .and(goeUpdatedAtStart(searchRequest.getUpdatedAtStart()))
                .and(loeUpdatedAtEnd(searchRequest.getUpdatedAtEnd()))
                .and(containsUpdatedBy(searchRequest.getUpdatedBy()))
                .and(eqId(searchRequest.getId()))
                .and(eqParentItemId(searchRequest.getParentItemId()))
                .and(eqItemId(searchRequest.getItemId()))
                .and(eqIsRoot(searchRequest.getIsRoot()))
                .and(eqParentProgressId(searchRequest.getParentProgressId()))
                .and(eqItemProgressId(searchRequest.getItemProgressId()))
                .and(containsInputUnitCode(searchRequest.getInputUnitCode()))
                .and(containsInputUnit(searchRequest.getInputUnit()))
                .and(mbom.isDelete.eq(false));

        List<Mbom> results = queryFactory
                .selectFrom(mbom)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(mbom.id.desc())
                .fetch();
                
        long total = queryFactory
                .selectFrom(mbom)
                .where(builder)
                .fetchCount();
                
        return new PageImpl<>(results, pageable, total);
    }

    
    private BooleanExpression goeCreatedAtStart(String createdAtStart) {
        return StringUtils.hasText(createdAtStart) ? mbom.createdAt.goe(LocalDateTime.parse(createdAtStart)) : null;
    }

    private BooleanExpression loeCreatedAtEnd(String createdAtEnd) {
        return StringUtils.hasText(createdAtEnd) ? mbom.createdAt.loe(LocalDateTime.parse(createdAtEnd)) : null;
    }

    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? mbom.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression goeUpdatedAtStart(String updatedAtStart) {
        return StringUtils.hasText(updatedAtStart) ? mbom.updatedAt.goe(LocalDateTime.parse(updatedAtStart)) : null;
    }

    private BooleanExpression loeUpdatedAtEnd(String updatedAtEnd) {
        return StringUtils.hasText(updatedAtEnd) ? mbom.updatedAt.loe(LocalDateTime.parse(updatedAtEnd)) : null;
    }

    private BooleanExpression containsUpdatedBy(String updatedBy) {
        return StringUtils.hasText(updatedBy) ? mbom.updatedBy.containsIgnoreCase(updatedBy) : null;
    }

    private BooleanExpression eqId(Long id) {
        return id != null ? mbom.id.eq(id) : null;
    }

    private BooleanExpression eqParentItemId(Long parentItemId) {
        return parentItemId != null ? mbom.parentItemId.eq(parentItemId) : null;
    }

    private BooleanExpression eqItemId(Long itemId) {
        return itemId != null ? mbom.itemId.eq(itemId) : null;
    }

    private BooleanExpression eqIsRoot(Boolean isRoot) {
        return isRoot != null ? mbom.isRoot.eq(isRoot) : null;
    }

    private BooleanExpression eqParentProgressId(Long parentProgressId) {
        return parentProgressId != null ? mbom.parentProgressId.eq(parentProgressId) : null;
    }

    private BooleanExpression eqItemProgressId(Long itemProgressId) {
        return itemProgressId != null ? mbom.itemProgressId.eq(itemProgressId) : null;
    }

    private BooleanExpression containsInputUnitCode(String inputUnitCode) {
        return StringUtils.hasText(inputUnitCode) ? mbom.inputUnitCode.containsIgnoreCase(inputUnitCode) : null;
    }

    private BooleanExpression containsInputUnit(String inputUnit) {
        return StringUtils.hasText(inputUnit) ? mbom.inputUnit.containsIgnoreCase(inputUnit) : null;
    }
}