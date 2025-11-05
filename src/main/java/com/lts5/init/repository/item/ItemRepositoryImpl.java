package com.lts5.init.repository.item;

import com.lts5.init.dto.FileLinkDto;
import com.lts5.init.entity.FileLink;
import com.lts5.init.entity.Item;
import com.lts5.init.payload.request.item.ItemSearchRequest;
import com.lts5.init.payload.response.item.ItemSearchResponse;
import com.primes.library.common.mapper.GlobalMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

import static com.lts5.init.entity.QFileLink.fileLink;
import static com.lts5.init.entity.QItem.item;
import com.lts5.init.entity.QCode;

public class ItemRepositoryImpl extends QuerydslRepositorySupport implements ItemRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    private final GlobalMapper globalMapper;
    
    public ItemRepositoryImpl(JPAQueryFactory queryFactory, GlobalMapper globalMapper) {
        super(Item.class);
        this.queryFactory = queryFactory;
        this.globalMapper = globalMapper;
    }
    
    @Override
    public Page<ItemSearchResponse> search(ItemSearchRequest searchRequest, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqIsUse(searchRequest.getIsUse()))
                .and(goeCreatedAtStart(searchRequest.getCreatedAtStart()))
                .and(loeCreatedAtEnd(searchRequest.getCreatedAtEnd()))
                .and(containsCreatedBy(searchRequest.getCreatedBy()))
                .and(goeUpdatedAtStart(searchRequest.getUpdatedAtStart()))
                .and(loeUpdatedAtEnd(searchRequest.getUpdatedAtEnd()))
                .and(containsUpdatedBy(searchRequest.getUpdatedBy()))
                .and(eqId(searchRequest.getId()))
                .and(eqItemNo(searchRequest.getItemNo()))
                .and(containsItemNumber(searchRequest.getItemNumber()))
                .and(containsItemName(searchRequest.getItemName()))
                .and(containsItemSpec(searchRequest.getItemSpec()))
                .and(containsItemModel(searchRequest.getItemModel()))
                .and(containsItemType1(searchRequest.getItemType1Code()))
                .and(containsItemType2(searchRequest.getItemType2Code()))
                .and(containsItemType3(searchRequest.getItemType3Code()))
                .and(containsItemUnit(searchRequest.getItemUnit()))
                .and(containsLotSize(searchRequest.getLotSizeCode()))
                .and(item.isDelete.eq(false));

        // JOIN으로 codeName 포함하여 조회 (별칭 사용)
        QCode code1 = new QCode("code1");
        QCode code2 = new QCode("code2");
        QCode code3 = new QCode("code3");
        QCode code4 = new QCode("code4");
        
        List<Tuple> results = queryFactory
                .select(item, code1.codeName, code2.codeName, code3.codeName, code4.codeName, fileLink)
                .from(item)
                .leftJoin(code1).on(code1.codeValue.eq(item.itemType1)
                        .and(code1.isDelete.eq(false))
                        .and(code1.isUse.eq(true)))
                .leftJoin(code2).on(code2.codeValue.eq(item.itemType2)
                        .and(code2.isDelete.eq(false))
                        .and(code2.isUse.eq(true)))
                .leftJoin(code3).on(code3.codeValue.eq(item.itemType3)
                        .and(code3.isDelete.eq(false))
                        .and(code3.isUse.eq(true)))
                .leftJoin(code4).on(code4.codeValue.eq(item.lotSize)
                        .and(code4.isDelete.eq(false))
                        .and(code4.isUse.eq(true)))
                .leftJoin(fileLink).on(fileLink.ownerId.eq(item.id)
                        .and(fileLink.ownerTable.eq("items"))
                        .and(fileLink.isDelete.eq(false)))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(item.id.desc())
                .fetch();
                
        long total = queryFactory
                .selectFrom(item)
                .where(builder)
                .fetchCount();
                
        // Tuple을 ItemSearchResponse DTO로 변환하여 반환 (FileLink 포함)
        Map<Long, ItemSearchResponse> itemMap = new LinkedHashMap<>();
        
        for (Tuple tuple : results) {
            Item itemResult = tuple.get(item);
            String itemType1Value = tuple.get(code1.codeName);
            String itemType2Value = tuple.get(code2.codeName);
            String itemType3Value = tuple.get(code3.codeName);
            String lotSizeValue = tuple.get(code4.codeName);
            FileLink fileLinkResult = tuple.get(fileLink);
            
            // Item이 이미 맵에 있는지 확인
            ItemSearchResponse response = itemMap.get(itemResult.getId());
            if (response == null) {
                // 새로운 Item인 경우
                List<FileLinkDto> fileUrls = new ArrayList<>();
                if (fileLinkResult != null) {
                    fileUrls.add(globalMapper.map(fileLinkResult, FileLinkDto.class));
                }
                response = globalMapper.map(itemResult, ItemSearchResponse.class);
                response.setItemType1Value(itemType1Value);
                response.setItemType2Value(itemType2Value);
                response.setItemType3Value(itemType3Value);
                response.setLotSizeValue(lotSizeValue);
                response.setFileUrls(fileUrls);
                itemMap.put(itemResult.getId(), response);
            } else {
                // 기존 Item에 FileLink 추가
                if (fileLinkResult != null && response.getFileUrls() != null) {
                    response.getFileUrls().add(globalMapper.map(fileLinkResult, FileLinkDto.class));
                }
            }
        }
        
        List<ItemSearchResponse> items = new ArrayList<>(itemMap.values());
                
        return new PageImpl<>(items, pageable, total);
    }

    @Override
    public List<ItemSearchResponse> searchAll(ItemSearchRequest searchRequest) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqIsUse(searchRequest.getIsUse()))
                .and(goeCreatedAtStart(searchRequest.getCreatedAtStart()))
                .and(loeCreatedAtEnd(searchRequest.getCreatedAtEnd()))
                .and(containsCreatedBy(searchRequest.getCreatedBy()))
                .and(goeUpdatedAtStart(searchRequest.getUpdatedAtStart()))
                .and(loeUpdatedAtEnd(searchRequest.getUpdatedAtEnd()))
                .and(containsUpdatedBy(searchRequest.getUpdatedBy()))
                .and(eqId(searchRequest.getId()))
                .and(eqItemNo(searchRequest.getItemNo()))
                .and(containsItemNumber(searchRequest.getItemNumber()))
                .and(containsItemName(searchRequest.getItemName()))
                .and(containsItemSpec(searchRequest.getItemSpec()))
                .and(containsItemModel(searchRequest.getItemModel()))
                .and(containsItemType1(searchRequest.getItemType1Code()))
                .and(containsItemType2(searchRequest.getItemType2Code()))
                .and(containsItemType3(searchRequest.getItemType3Code()))
                .and(containsItemUnit(searchRequest.getItemUnit()))
                .and(containsLotSize(searchRequest.getLotSizeCode()))
                .and(item.isDelete.eq(false));

        // JOIN으로 codeName 포함하여 조회 (별칭 사용)
        QCode code1 = new QCode("code1");
        QCode code2 = new QCode("code2");
        QCode code3 = new QCode("code3");
        QCode code4 = new QCode("code4");
        
        List<Tuple> results = queryFactory
                .select(item, code1.codeName, code2.codeName, code3.codeName, code4.codeName, fileLink)
                .from(item)
                .leftJoin(code1).on(code1.codeValue.eq(item.itemType1)
                        .and(code1.isDelete.eq(false))
                        .and(code1.isUse.eq(true)))
                .leftJoin(code2).on(code2.codeValue.eq(item.itemType2)
                        .and(code2.isDelete.eq(false))
                        .and(code2.isUse.eq(true)))
                .leftJoin(code3).on(code3.codeValue.eq(item.itemType3)
                        .and(code3.isDelete.eq(false))
                        .and(code3.isUse.eq(true)))
                .leftJoin(code4).on(code4.codeValue.eq(item.lotSize)
                        .and(code4.isDelete.eq(false))
                        .and(code4.isUse.eq(true)))
                .leftJoin(fileLink).on(fileLink.ownerId.eq(item.id)
                        .and(fileLink.ownerTable.eq("items"))
                        .and(fileLink.isDelete.eq(false)))
                .where(builder)
                .orderBy(item.id.desc())
                .fetch();
                
        // Tuple을 ItemSearchResponse DTO로 변환하여 반환 (FileLink 포함)
        Map<Long, ItemSearchResponse> itemMap = new LinkedHashMap<>();
        
        for (Tuple tuple : results) {
            Item itemResult = tuple.get(item);
            String itemType1Value = tuple.get(code1.codeName);
            String itemType2Value = tuple.get(code2.codeName);
            String itemType3Value = tuple.get(code3.codeName);
            String lotSizeValue = tuple.get(code4.codeName);
            FileLink fileLinkResult = tuple.get(fileLink);
            
            // Item이 이미 맵에 있는지 확인
            ItemSearchResponse response = itemMap.get(itemResult.getId());
            if (response == null) {
                // 새로운 Item인 경우
                List<FileLinkDto> fileUrls = new ArrayList<>();
                if (fileLinkResult != null) {
                    fileUrls.add(globalMapper.map(fileLinkResult, FileLinkDto.class));
                }
                response = globalMapper.map(itemResult, ItemSearchResponse.class);
                response.setItemType1Value(itemType1Value);
                response.setItemType2Value(itemType2Value);
                response.setItemType3Value(itemType3Value);
                response.setLotSizeValue(lotSizeValue);
                response.setFileUrls(fileUrls);
                itemMap.put(itemResult.getId(), response);
            } else {
                // 기존 Item에 FileLink 추가
                if (fileLinkResult != null && response.getFileUrls() != null) {
                    response.getFileUrls().add(globalMapper.map(fileLinkResult, FileLinkDto.class));
                }
            }
        }
        
        return new ArrayList<>(itemMap.values());
    }

    
    private BooleanExpression eqIsUse(Boolean isUse) {
        return isUse != null ? item.isUse.eq(isUse) : null;
    }

    private BooleanExpression goeCreatedAtStart(String createdAtStart) {
        return StringUtils.hasText(createdAtStart) ? item.createdAt.goe(LocalDateTime.parse(createdAtStart)) : null;
    }

    private BooleanExpression loeCreatedAtEnd(String createdAtEnd) {
        return StringUtils.hasText(createdAtEnd) ? item.createdAt.loe(LocalDateTime.parse(createdAtEnd)) : null;
    }

    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? item.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression goeUpdatedAtStart(String updatedAtStart) {
        return StringUtils.hasText(updatedAtStart) ? item.updatedAt.goe(LocalDateTime.parse(updatedAtStart)) : null;
    }

    private BooleanExpression loeUpdatedAtEnd(String updatedAtEnd) {
        return StringUtils.hasText(updatedAtEnd) ? item.updatedAt.loe(LocalDateTime.parse(updatedAtEnd)) : null;
    }

    private BooleanExpression containsUpdatedBy(String updatedBy) {
        return StringUtils.hasText(updatedBy) ? item.updatedBy.containsIgnoreCase(updatedBy) : null;
    }

    private BooleanExpression eqId(Long id) {
        return id != null ? item.id.eq(id) : null;
    }

    private BooleanExpression eqItemNo(Short itemNo) {
        return itemNo != null ? item.itemNo.eq(itemNo) : null;
    }

    private BooleanExpression containsItemNumber(String itemNumber) {
        return StringUtils.hasText(itemNumber) ? item.itemNumber.containsIgnoreCase(itemNumber) : null;
    }

    private BooleanExpression containsItemName(String itemName) {
        return StringUtils.hasText(itemName) ? item.itemName.containsIgnoreCase(itemName) : null;
    }

    private BooleanExpression containsItemSpec(String itemSpec) {
        return StringUtils.hasText(itemSpec) ? item.itemSpec.containsIgnoreCase(itemSpec) : null;
    }

    private BooleanExpression containsItemModel(String itemModel) {
        return StringUtils.hasText(itemModel) ? item.itemModel.containsIgnoreCase(itemModel) : null;
    }

    private BooleanExpression containsItemType1(String itemType1) {
        return StringUtils.hasText(itemType1) ? item.itemType1.containsIgnoreCase(itemType1) : null;
    }

    private BooleanExpression containsItemType2(String itemType2) {
        return StringUtils.hasText(itemType2) ? item.itemType2.containsIgnoreCase(itemType2) : null;
    }

    private BooleanExpression containsItemType3(String itemType3) {
        return StringUtils.hasText(itemType3) ? item.itemType3.containsIgnoreCase(itemType3) : null;
    }

    private BooleanExpression containsItemUnit(String itemUnit) {
        return StringUtils.hasText(itemUnit) ? item.itemUnit.containsIgnoreCase(itemUnit) : null;
    }

    private BooleanExpression containsLotSize(String lotSize) {
        return StringUtils.hasText(lotSize) ? item.lotSize.containsIgnoreCase(lotSize) : null;
    }
}