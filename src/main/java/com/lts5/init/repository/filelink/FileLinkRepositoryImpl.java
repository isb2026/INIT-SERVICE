package com.lts5.init.repository.filelink;

import com.lts5.init.entity.FileLink;
import com.lts5.init.entity.enums.OwnerType;
import com.lts5.init.payload.request.filelink.FileLinkSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.lts5.init.entity.QFileLink.fileLink;

@Repository
public class FileLinkRepositoryImpl extends QuerydslRepositorySupport implements FileLinkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public FileLinkRepositoryImpl(JPAQueryFactory queryFactory) {
        super(FileLink.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<FileLink> search(FileLinkSearchRequest searchRequest, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqIsUse(searchRequest.getIsUse()))
                .and(goeCreatedAtStart(searchRequest.getCreatedAtStart()))
                .and(loeCreatedAtEnd(searchRequest.getCreatedAtEnd()))
                .and(containsCreatedBy(searchRequest.getCreatedBy()))
                .and(goeUpdatedAtStart(searchRequest.getUpdatedAtStart()))
                .and(loeUpdatedAtEnd(searchRequest.getUpdatedAtEnd()))
                .and(containsUpdatedBy(searchRequest.getUpdatedBy()))
                .and(containsOwnerTable(searchRequest.getOwnerTable()))
                .and(eqOwnerType(searchRequest.getOwnerType()))
                .and(eqOwnerId(searchRequest.getOwnerId()))
                .and(containsUrl(searchRequest.getUrl()))
                .and(eqSortOrder(searchRequest.getSortOrder()))
                .and(eqIsPrimary(searchRequest.getIsPrimary()))
                .and(eqId(searchRequest.getId()))
                .and(fileLink.isDelete.eq(false));

        List<FileLink> results = queryFactory
                .selectFrom(fileLink)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(fileLink.id.desc())
                .fetch();

        long total = queryFactory
                .selectFrom(fileLink)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    // ==================== 조건 구성 유틸 ====================

    private BooleanExpression eqIsUse(Boolean isUse) {
        return isUse != null ? fileLink.isUse.eq(isUse) : null;
    }

    private BooleanExpression eqId(Long id) {
        return id != null ? fileLink.id.eq(id) : null;
    }

    private BooleanExpression containsOwnerTable(String ownerTable) {
        return StringUtils.hasText(ownerTable) ? fileLink.ownerTable.containsIgnoreCase(ownerTable) : null;
    }

    private BooleanExpression eqOwnerType(OwnerType ownerType) {
        return ownerType != null ? fileLink.ownerType.eq(ownerType) : null;
    }

    private BooleanExpression eqOwnerId(Long ownerId) {
        return ownerId != null ? fileLink.ownerId.eq(ownerId) : null;
    }

    private BooleanExpression containsUrl(String url) {
        return StringUtils.hasText(url) ? fileLink.url.containsIgnoreCase(url) : null;
    }

    private BooleanExpression eqSortOrder(Short sortOrder) {
        return sortOrder != null ? fileLink.sortOrder.eq(sortOrder) : null;
    }

    private BooleanExpression eqIsPrimary(Boolean isPrimary) {
        return isPrimary != null ? fileLink.isPrimary.eq(isPrimary) : null;
    }

    private BooleanExpression containsDescription(String description) {
        return StringUtils.hasText(description) ? fileLink.description.containsIgnoreCase(description) : null;
    }

    private BooleanExpression goeCreatedAtStart(String createdAtStart) {
        return StringUtils.hasText(createdAtStart) ? fileLink.createdAt.goe(LocalDateTime.parse(createdAtStart)) : null;
    }

    private BooleanExpression loeCreatedAtEnd(String createdAtEnd) {
        return StringUtils.hasText(createdAtEnd) ? fileLink.createdAt.loe(LocalDateTime.parse(createdAtEnd)) : null;
    }

    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? fileLink.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression goeUpdatedAtStart(String updatedAtStart) {
        return StringUtils.hasText(updatedAtStart) ? fileLink.updatedAt.goe(LocalDateTime.parse(updatedAtStart)) : null;
    }

    private BooleanExpression loeUpdatedAtEnd(String updatedAtEnd) {
        return StringUtils.hasText(updatedAtEnd) ? fileLink.updatedAt.loe(LocalDateTime.parse(updatedAtEnd)) : null;
    }

    private BooleanExpression containsUpdatedBy(String updatedBy) {
        return StringUtils.hasText(updatedBy) ? fileLink.updatedBy.containsIgnoreCase(updatedBy) : null;
    }
}