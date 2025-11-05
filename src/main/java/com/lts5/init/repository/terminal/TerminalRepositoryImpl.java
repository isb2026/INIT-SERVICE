package com.lts5.init.repository.terminal;

import com.lts5.init.entity.Terminal;
import com.lts5.init.payload.request.terminal.TerminalSearchRequest;
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

import static com.lts5.init.entity.QTerminal.terminal;

public class TerminalRepositoryImpl extends QuerydslRepositorySupport implements TerminalRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    public TerminalRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Terminal.class);
        this.queryFactory = queryFactory;
    }
    
    @Override
    public Page<Terminal> search(TerminalSearchRequest searchRequest, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqIsUse(searchRequest.getIsUse()))
                .and(goeCreatedAtStart(searchRequest.getCreatedAtStart()))
                .and(loeCreatedAtEnd(searchRequest.getCreatedAtEnd()))
                .and(containsCreatedBy(searchRequest.getCreatedBy()))
                .and(goeUpdatedAtStart(searchRequest.getUpdatedAtStart()))
                .and(loeUpdatedAtEnd(searchRequest.getUpdatedAtEnd()))
                .and(containsUpdatedBy(searchRequest.getUpdatedBy()))
                .and(containsTerminalCode(searchRequest.getTerminalCode()))
                .and(containsTerminalName(searchRequest.getTerminalName()))
                .and(containsDescription(searchRequest.getDescription()))
                .and(containsImageUrl(searchRequest.getImageUrl()))
                .and(terminal.isDelete.eq(false));

        List<Terminal> results = queryFactory
                .selectFrom(terminal)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(terminal.id.desc())
                .fetch();
                
        long total = queryFactory
                .selectFrom(terminal)
                .where(builder)
                .fetchCount();
                
        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public List<Terminal> searchAll(TerminalSearchRequest searchRequest) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqIsUse(searchRequest.getIsUse()))
                .and(goeCreatedAtStart(searchRequest.getCreatedAtStart()))
                .and(loeCreatedAtEnd(searchRequest.getCreatedAtEnd()))
                .and(containsCreatedBy(searchRequest.getCreatedBy()))
                .and(goeUpdatedAtStart(searchRequest.getUpdatedAtStart()))
                .and(loeUpdatedAtEnd(searchRequest.getUpdatedAtEnd()))
                .and(containsUpdatedBy(searchRequest.getUpdatedBy()))
                .and(containsTerminalCode(searchRequest.getTerminalCode()))
                .and(containsTerminalName(searchRequest.getTerminalName()))
                .and(containsDescription(searchRequest.getDescription()))
                .and(containsImageUrl(searchRequest.getImageUrl()))
                .and(terminal.isDelete.eq(false));

        return queryFactory
                .selectFrom(terminal)
                .where(builder)
                .orderBy(terminal.id.desc())
                .fetch();
    }

    
    private BooleanExpression eqIsUse(Boolean isUse) {
        return isUse != null ? terminal.isUse.eq(isUse) : null;
    }

    private BooleanExpression goeCreatedAtStart(String createdAtStart) {
        return StringUtils.hasText(createdAtStart) ? terminal.createdAt.goe(LocalDateTime.parse(createdAtStart)) : null;
    }

    private BooleanExpression loeCreatedAtEnd(String createdAtEnd) {
        return StringUtils.hasText(createdAtEnd) ? terminal.createdAt.loe(LocalDateTime.parse(createdAtEnd)) : null;
    }

    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? terminal.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression goeUpdatedAtStart(String updatedAtStart) {
        return StringUtils.hasText(updatedAtStart) ? terminal.updatedAt.goe(LocalDateTime.parse(updatedAtStart)) : null;
    }

    private BooleanExpression loeUpdatedAtEnd(String updatedAtEnd) {
        return StringUtils.hasText(updatedAtEnd) ? terminal.updatedAt.loe(LocalDateTime.parse(updatedAtEnd)) : null;
    }

    private BooleanExpression containsUpdatedBy(String updatedBy) {
        return StringUtils.hasText(updatedBy) ? terminal.updatedBy.containsIgnoreCase(updatedBy) : null;
    }

    private BooleanExpression eqId(Long id) {
        return id != null ? terminal.id.eq(id) : null;
    }

    private BooleanExpression eqAccountYear(Short accountYear) {
        // accountYear is now @Transient, so filtering is not supported at database level
        return null;
    }

    private BooleanExpression containsTerminalCode(String terminalCode) {
        return StringUtils.hasText(terminalCode) ? terminal.terminalCode.containsIgnoreCase(terminalCode) : null;
    }

    private BooleanExpression containsTerminalName(String terminalName) {
        return StringUtils.hasText(terminalName) ? terminal.terminalName.containsIgnoreCase(terminalName) : null;
    }

    private BooleanExpression containsDescription(String description) {
        return StringUtils.hasText(description) ? terminal.description.containsIgnoreCase(description) : null;
    }

    private BooleanExpression containsImageUrl(String imageUrl) {
        return StringUtils.hasText(imageUrl) ? terminal.imageUrl.containsIgnoreCase(imageUrl) : null;
    }
}