package com.lts5.init.repository.dataset;

import com.lts5.init.entity.DatasetVersion;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class DatasetVersionRepositoryImpl extends QuerydslRepositorySupport implements DatasetVersionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DatasetVersionRepositoryImpl(JPAQueryFactory queryFactory) {
        super(DatasetVersion.class);
        this.queryFactory = queryFactory;
    }
} 