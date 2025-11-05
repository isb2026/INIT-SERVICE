package com.lts5.init.repository.itemprogress;

import com.lts5.init.entity.ItemProgress;
import com.lts5.init.payload.request.itemprogress.ItemProgressSearchRequest;
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

import static com.lts5.init.entity.QItemProgress.itemProgress;

public class ItemProgressRepositoryImpl extends QuerydslRepositorySupport implements ItemProgressRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    public ItemProgressRepositoryImpl(JPAQueryFactory queryFactory) {
        super(ItemProgress.class);
        this.queryFactory = queryFactory;
    }
    
    @Override
    public Page<ItemProgress> search(ItemProgressSearchRequest searchRequest, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqIsUse(searchRequest.getIsUse()))
                .and(goeCreatedAtStart(searchRequest.getCreatedAtStart()))
                .and(loeCreatedAtEnd(searchRequest.getCreatedAtEnd()))
                .and(containsCreatedBy(searchRequest.getCreatedBy()))
                .and(goeUpdatedAtStart(searchRequest.getUpdatedAtStart()))
                .and(loeUpdatedAtEnd(searchRequest.getUpdatedAtEnd()))
                .and(containsUpdatedBy(searchRequest.getUpdatedBy()))
                .and(eqId(searchRequest.getId()))
                .and(eqItemId(searchRequest.getItemId()))
                .and(containsProgressName(searchRequest.getProgressName()))
                .and(eqIsOutsourcing(searchRequest.getIsOutsourcing()))
                .and(eqProgressTypeCode(searchRequest.getProgressTypeCode()))
                .and(containsProgressTypeName(searchRequest.getProgressTypeName()))
                .and(eqUnitWeight(searchRequest.getUnitWeight()))
                .and(containsUnitTypeName(searchRequest.getUnitTypeName()))
                .and(eqUnitTypeCode(searchRequest.getUnitTypeCode()))
                .and(eqDefaultCycleTime(searchRequest.getDefaultCycleTime()))
                .and(eqOptimalProgressInventoryQty(searchRequest.getOptimalProgressInventoryQty()))
                .and(eqSafetyProgressInventoryQty(searchRequest.getSafetyProgressInventoryQty()))
                .and(containsProgressDefaultSpec(searchRequest.getProgressDefaultSpec()))
                .and(containsKeyManagementContents(searchRequest.getKeyManagementContents()))
                .and(itemProgress.isDelete.eq(false));

        List<ItemProgress> results = queryFactory
                .selectFrom(itemProgress)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(itemProgress.id.desc(), itemProgress.progressOrder.asc())
                .fetch();
                
        long total = queryFactory
                .selectFrom(itemProgress)
                .where(builder)
                .fetchCount();
                
        return new PageImpl<>(results, pageable, total);
    }

    
    private BooleanExpression eqIsUse(Boolean isUse) {
        return isUse != null ? itemProgress.isUse.eq(isUse) : null;
    }

    private BooleanExpression goeCreatedAtStart(String createdAtStart) {
        return StringUtils.hasText(createdAtStart) ? itemProgress.createdAt.goe(LocalDateTime.parse(createdAtStart)) : null;
    }

    private BooleanExpression loeCreatedAtEnd(String createdAtEnd) {
        return StringUtils.hasText(createdAtEnd) ? itemProgress.createdAt.loe(LocalDateTime.parse(createdAtEnd)) : null;
    }

    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? itemProgress.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression goeUpdatedAtStart(String updatedAtStart) {
        return StringUtils.hasText(updatedAtStart) ? itemProgress.updatedAt.goe(LocalDateTime.parse(updatedAtStart)) : null;
    }

    private BooleanExpression loeUpdatedAtEnd(String updatedAtEnd) {
        return StringUtils.hasText(updatedAtEnd) ? itemProgress.updatedAt.loe(LocalDateTime.parse(updatedAtEnd)) : null;
    }

    private BooleanExpression containsUpdatedBy(String updatedBy) {
        return StringUtils.hasText(updatedBy) ? itemProgress.updatedBy.containsIgnoreCase(updatedBy) : null;
    }

    private BooleanExpression eqId(Long id) {
        return id != null ? itemProgress.id.eq(id) : null;
    }

    private BooleanExpression eqAccountYear(Short accountYear) {
        // accountYear is now @Transient, so filtering is not supported at database level
        return null;
    }

    private BooleanExpression eqItemId(Long itemId) {
        return itemId != null ? itemProgress.item.id.eq(itemId) : null;
    }

    private BooleanExpression containsProgressName(String progressName) {
        return StringUtils.hasText(progressName) ? itemProgress.progressName.containsIgnoreCase(progressName) : null;
    }

    private BooleanExpression eqIsOutsourcing(Boolean isOutsourcing) {
        return isOutsourcing != null ? itemProgress.isOutsourcing.eq(isOutsourcing) : null;
    }

    private BooleanExpression eqProgressTypeCode(String progressTypeCode) {
        return StringUtils.hasText(progressTypeCode) ? itemProgress.progressTypeCode.eq(progressTypeCode) : null;
    }

    private BooleanExpression containsProgressTypeName(String progressTypeName) {
        return StringUtils.hasText(progressTypeName) ? itemProgress.progressTypeName.containsIgnoreCase(progressTypeName) : null;
    }

    private BooleanExpression eqDefaultCycleTime(Float defaultCycleTime) {
        return defaultCycleTime != null ? itemProgress.defaultCycleTime.eq(defaultCycleTime) : null;
    }

    private BooleanExpression eqOptimalProgressInventoryQty(Float optimalProgressInventoryQty) {
        return optimalProgressInventoryQty != null ? itemProgress.optimalProgressInventoryQty.eq(optimalProgressInventoryQty) : null;
    }

    private BooleanExpression eqSafetyProgressInventoryQty(Float safetyProgressInventoryQty) {
        return safetyProgressInventoryQty != null ? itemProgress.safetyProgressInventoryQty.eq(safetyProgressInventoryQty) : null;
    }

    private BooleanExpression containsProgressDefaultSpec(String progressDefaultSpec) {
        return StringUtils.hasText(progressDefaultSpec) ? itemProgress.progressDefaultSpec.containsIgnoreCase(progressDefaultSpec) : null;
    }

    private BooleanExpression containsKeyManagementContents(String keyManagementContents) {
        return StringUtils.hasText(keyManagementContents) ? itemProgress.keyManagementContents.containsIgnoreCase(keyManagementContents) : null;
    }

    private BooleanExpression eqUnitWeight(Float unitWeight) {
        return unitWeight != null ? itemProgress.unitWeight.eq(unitWeight) : null;
    }

    private BooleanExpression containsUnitTypeName(String unitTypeName) {
        return StringUtils.hasText(unitTypeName) ? itemProgress.unitTypeName.containsIgnoreCase(unitTypeName) : null;
    }

    private BooleanExpression eqUnitTypeCode(String unitTypeCode) {
        return StringUtils.hasText(unitTypeCode) ? itemProgress.unitTypeCode.eq(unitTypeCode) : null;
    }
}