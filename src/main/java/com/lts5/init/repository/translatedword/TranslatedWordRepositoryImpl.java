package com.lts5.init.repository.translatedword;

import com.lts5.init.entity.TranslatedWord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class TranslatedWordRepositoryImpl extends QuerydslRepositorySupport implements TranslatedWordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public TranslatedWordRepositoryImpl(JPAQueryFactory queryFactory) {
        super(TranslatedWord.class);
        this.queryFactory = queryFactory;
    }
} 