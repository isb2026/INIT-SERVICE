package com.lts5.init.repository.rootword;

import com.lts5.init.entity.RootWord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class RootWordRepositoryImpl extends QuerydslRepositorySupport implements RootWordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RootWordRepositoryImpl(JPAQueryFactory queryFactory) {
        super(RootWord.class);
        this.queryFactory = queryFactory;
    }
} 