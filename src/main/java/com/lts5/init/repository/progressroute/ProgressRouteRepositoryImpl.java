package com.lts5.init.repository.progressroute;

import com.lts5.init.entity.ProgressRoute;
import com.lts5.init.payload.request.progressroute.ProgressRouteSearchRequest;
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

import static com.lts5.init.entity.QProgressRoute.progressRoute;

public class ProgressRouteRepositoryImpl extends QuerydslRepositorySupport implements ProgressRouteRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    public ProgressRouteRepositoryImpl(JPAQueryFactory queryFactory) {
        super(ProgressRoute.class);
        this.queryFactory = queryFactory;
    }
    
    @Override
    public Page<ProgressRoute> search(ProgressRouteSearchRequest searchRequest, Pageable pageable) {
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
                .and(eqProgressSequence(searchRequest.getProgressSequence()))
                .and(eqProgressTypeCode(searchRequest.getProgressTypeCode()))
                .and(eqProgressTypeName(searchRequest.getProgressTypeName()))
                .and(eqProgressRealName(searchRequest.getProgressRealName()))
                .and(eqDefaultCycleTime(searchRequest.getDefaultCycleTime()))
                .and(eqLotSize(searchRequest.getLotSize()))
                .and(eqLotUnit(searchRequest.getLotUnit()))
                .and(eqOptimalProgressInventoryQty(searchRequest.getOptimalProgressInventoryQty()))
                .and(eqSafetyProgressInventoryQty(searchRequest.getSafetyProgressInventoryQty()))
                .and(eqProgressDefaultSpec(searchRequest.getProgressDefaultSpec()))
                .and(eqKeyManagementContents(searchRequest.getKeyManagementContents()))
                .and(progressRoute.isDelete.eq(false));

        List<ProgressRoute> results = queryFactory
                .selectFrom(progressRoute)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(progressRoute.id.desc())
                .fetch();
                
        long total = queryFactory
                .selectFrom(progressRoute)
                .where(builder)
                .fetchCount();
                
        return new PageImpl<>(results, pageable, total);
    }

    
    private BooleanExpression eqIsUse(Boolean isUse) {
        return isUse != null ? progressRoute.isUse.eq(isUse) : null;
    }

    private BooleanExpression goeCreatedAtStart(String createdAtStart) {
        return StringUtils.hasText(createdAtStart) ? progressRoute.createdAt.goe(LocalDateTime.parse(createdAtStart)) : null;
    }

    private BooleanExpression loeCreatedAtEnd(String createdAtEnd) {
        return StringUtils.hasText(createdAtEnd) ? progressRoute.createdAt.loe(LocalDateTime.parse(createdAtEnd)) : null;
    }

    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? progressRoute.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression goeUpdatedAtStart(String updatedAtStart) {
        return StringUtils.hasText(updatedAtStart) ? progressRoute.updatedAt.goe(LocalDateTime.parse(updatedAtStart)) : null;
    }

    private BooleanExpression loeUpdatedAtEnd(String updatedAtEnd) {
        return StringUtils.hasText(updatedAtEnd) ? progressRoute.updatedAt.loe(LocalDateTime.parse(updatedAtEnd)) : null;
    }

    private BooleanExpression containsUpdatedBy(String updatedBy) {
        return StringUtils.hasText(updatedBy) ? progressRoute.updatedBy.containsIgnoreCase(updatedBy) : null;
    }

    private BooleanExpression eqId(Long id) {
        return id != null ? progressRoute.id.eq(id) : null;
    }

    private BooleanExpression eqItemId(Long itemId) {
        return itemId != null ? progressRoute.item.id.eq(itemId) : null;
    }

    private BooleanExpression eqProgressSequence(Byte progressSequence) {
        return progressSequence != null ? progressRoute.progressSequence.eq(progressSequence) : null;
    }

    private BooleanExpression eqProgressTypeCode(String progressTypeCode) {
        return StringUtils.hasText(progressTypeCode) ? progressRoute.progressTypeCode.containsIgnoreCase(progressTypeCode) : null;
    }

    private BooleanExpression eqProgressTypeName(String progressTypeName) {
        return StringUtils.hasText(progressTypeName) ? progressRoute.progressTypeName.containsIgnoreCase(progressTypeName) : null;
    }

    private BooleanExpression eqProgressRealName(String progressRealName) {
        return StringUtils.hasText(progressRealName) ? progressRoute.progressRealName.containsIgnoreCase(progressRealName) : null;
    }

    private BooleanExpression eqDefaultCycleTime(Double defaultCycleTime) {
        return defaultCycleTime != null ? progressRoute.defaultCycleTime.eq(defaultCycleTime) : null;
    }

    private BooleanExpression eqLotSize(Double lotSize) {
        return lotSize != null ? progressRoute.lotSize.eq(lotSize) : null;
    }

    private BooleanExpression eqLotUnit(String lotUnit) {
        return StringUtils.hasText(lotUnit) ? progressRoute.lotUnit.containsIgnoreCase(lotUnit) : null;
    }

    private BooleanExpression eqOptimalProgressInventoryQty(Double optimalProgressInventoryQty) {
        return optimalProgressInventoryQty != null ? progressRoute.optimalProgressInventoryQty.eq(optimalProgressInventoryQty) : null;
    }

    private BooleanExpression eqSafetyProgressInventoryQty(Double safetyProgressInventoryQty) {
        return safetyProgressInventoryQty != null ? progressRoute.safetyProgressInventoryQty.eq(safetyProgressInventoryQty) : null;
    }

    private BooleanExpression eqProgressDefaultSpec(String progressDefaultSpec) {
        return StringUtils.hasText(progressDefaultSpec) ? progressRoute.progressDefaultSpec.containsIgnoreCase(progressDefaultSpec) : null;
    }

    private BooleanExpression eqKeyManagementContents(String keyManagementContents) {
        return StringUtils.hasText(keyManagementContents) ? progressRoute.keyManagementContents.containsIgnoreCase(keyManagementContents) : null;
    }
}