package com.lts5.init.repository.language;

import com.lts5.init.entity.Language;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Optional;

public class LanguageRepositoryImpl extends QuerydslRepositorySupport implements LanguageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public LanguageRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Language.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<Language> findByIsoCode(String isoCode) {
        // TODO: Implement with QueryDSL once Q-classes are generated
        return Optional.empty();
    }
} 