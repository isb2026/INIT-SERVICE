package com.lts5.init.repository.vendor;

import com.lts5.init.entity.Vendor;
import com.lts5.init.dto.VendorDto;
import com.lts5.init.payload.request.vendor.VendorSearchRequest;
import com.primes.library.common.mapper.GlobalMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lts5.init.entity.QVendor.vendor;
import static com.lts5.init.entity.QCode.code;

public class VendorRepositoryImpl extends QuerydslRepositorySupport implements VendorRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    private final GlobalMapper globalMapper;
    
    public VendorRepositoryImpl(JPAQueryFactory queryFactory, GlobalMapper globalMapper) {
        super(Vendor.class);
        this.queryFactory = queryFactory;
        this.globalMapper = globalMapper;
    }
    
    @Override
    public Page<VendorDto> search(VendorSearchRequest searchRequest, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqIsUse(searchRequest.getIsUse()))
                .and(goeCreatedAtStart(searchRequest.getCreatedAtStart()))
                .and(loeCreatedAtEnd(searchRequest.getCreatedAtEnd()))
                .and(containsCreatedBy(searchRequest.getCreatedBy()))
                .and(goeUpdatedAtStart(searchRequest.getUpdatedAtStart()))
                .and(loeUpdatedAtEnd(searchRequest.getUpdatedAtEnd()))
                .and(containsUpdatedBy(searchRequest.getUpdatedBy()))
                .and(eqId(searchRequest.getId()))
                .and(containsCompCode(searchRequest.getCompCode()))
                .and(containsCompType(searchRequest.getCompType()))
                .and(containsLicenseNo(searchRequest.getLicenseNo()))
                .and(containsCompName(searchRequest.getCompName()))
                .and(containsCeoName(searchRequest.getCeoName()))
                .and(containsCompEmail(searchRequest.getCompEmail()))
                .and(containsTelNumber(searchRequest.getTelNumber()))
                .and(containsFaxNumber(searchRequest.getFaxNumber()))
                .and(containsZipCode(searchRequest.getZipCode()))
                .and(containsAddressDtl(searchRequest.getAddressDtl()))
                .and(containsAddressMst(searchRequest.getAddressMst()))
                .and(vendor.isDelete.eq(false));

        // fetchJoin으로 code와 함께 조회하여 compTypeName 설정
        List<Tuple> results = queryFactory
                .select(vendor, code.codeName)
                .from(vendor)
                .leftJoin(code).on(code.codeValue.eq(vendor.compType)
                        .and(code.isDelete.eq(false))
                        .and(code.isUse.eq(true)))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(vendor.id.desc())
                .fetch();
                
        long total = Optional.ofNullable(queryFactory
                .select(vendor.count())
                .from(vendor)
                .where(builder)
                .fetchOne()).orElse(0L);

        // Tuple을 VendorDto로 변환하여 반환
        List<VendorDto> vendorDtos = results.stream()
                .map(tuple -> {
                    Vendor vendorResult = tuple.get(vendor);
                    String compTypeName = tuple.get(code.codeName);

                    // GlobalMapper로 VendorDto 생성
                    VendorDto vendorDto = globalMapper.map(vendorResult, VendorDto.class);
                    vendorDto.setCompTypeName(compTypeName);

                    return vendorDto;
                })
                .collect(Collectors.toList());
                
        return new PageImpl<>(vendorDtos, pageable, total);
    }

    @Override
    public List<VendorDto> searchAll(VendorSearchRequest searchRequest) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqIsUse(searchRequest.getIsUse()))
                .and(goeCreatedAtStart(searchRequest.getCreatedAtStart()))
                .and(loeCreatedAtEnd(searchRequest.getCreatedAtEnd()))
                .and(containsCreatedBy(searchRequest.getCreatedBy()))
                .and(goeUpdatedAtStart(searchRequest.getUpdatedAtStart()))
                .and(loeUpdatedAtEnd(searchRequest.getUpdatedAtEnd()))
                .and(containsUpdatedBy(searchRequest.getUpdatedBy()))
                .and(eqId(searchRequest.getId()))
                .and(containsCompCode(searchRequest.getCompCode()))
                .and(containsCompType(searchRequest.getCompType()))
                .and(containsLicenseNo(searchRequest.getLicenseNo()))
                .and(containsCompName(searchRequest.getCompName()))
                .and(containsCeoName(searchRequest.getCeoName()))
                .and(containsCompEmail(searchRequest.getCompEmail()))
                .and(containsTelNumber(searchRequest.getTelNumber()))
                .and(containsFaxNumber(searchRequest.getFaxNumber()))
                .and(containsZipCode(searchRequest.getZipCode()))
                .and(containsAddressDtl(searchRequest.getAddressDtl()))
                .and(containsAddressMst(searchRequest.getAddressMst()))
                .and(vendor.isDelete.eq(false));

        // fetchJoin으로 code와 함께 조회하여 compTypeName 설정
        List<Tuple> results = queryFactory
                .select(vendor, code.codeName)
                .from(vendor)
                .leftJoin(code).on(code.codeValue.eq(vendor.compType)
                        .and(code.isDelete.eq(false))
                        .and(code.isUse.eq(true)))
                .where(builder)
                .orderBy(vendor.id.desc())
                .fetch();

        // Tuple을 VendorDto로 변환하여 반환
        return results.stream()
                .map(tuple -> {
                    Vendor vendorResult = tuple.get(vendor);
                    String compTypeName = tuple.get(code.codeName);

                    // GlobalMapper로 VendorDto 생성
                    VendorDto vendorDto = globalMapper.map(vendorResult, VendorDto.class);
                    vendorDto.setCompTypeName(compTypeName);

                    return vendorDto;
                })
                .collect(Collectors.toList());
    }

    
    private BooleanExpression eqIsUse(Boolean isUse) {
        return isUse != null ? vendor.isUse.eq(isUse) : null;
    }

    private BooleanExpression goeCreatedAtStart(String createdAtStart) {
        return StringUtils.hasText(createdAtStart) ? vendor.createdAt.goe(LocalDateTime.parse(createdAtStart)) : null;
    }

    private BooleanExpression loeCreatedAtEnd(String createdAtEnd) {
        return StringUtils.hasText(createdAtEnd) ? vendor.createdAt.loe(LocalDateTime.parse(createdAtEnd)) : null;
    }

    private BooleanExpression containsCreatedBy(String createdBy) {
        return StringUtils.hasText(createdBy) ? vendor.createdBy.containsIgnoreCase(createdBy) : null;
    }

    private BooleanExpression goeUpdatedAtStart(String updatedAtStart) {
        return StringUtils.hasText(updatedAtStart) ? vendor.updatedAt.goe(LocalDateTime.parse(updatedAtStart)) : null;
    }

    private BooleanExpression loeUpdatedAtEnd(String updatedAtEnd) {
        return StringUtils.hasText(updatedAtEnd) ? vendor.updatedAt.loe(LocalDateTime.parse(updatedAtEnd)) : null;
    }

    private BooleanExpression containsUpdatedBy(String updatedBy) {
        return StringUtils.hasText(updatedBy) ? vendor.updatedBy.containsIgnoreCase(updatedBy) : null;
    }

    private BooleanExpression eqId(Long id) {
        return id != null ? vendor.id.eq(id) : null;
    }

    private BooleanExpression containsCompCode(String compCode) {
        return StringUtils.hasText(compCode) ? vendor.compCode.containsIgnoreCase(compCode) : null;
    }

    private BooleanExpression containsCompType(String compType) {
        return StringUtils.hasText(compType) ? vendor.compType.containsIgnoreCase(compType) : null;
    }

    private BooleanExpression containsLicenseNo(String licenseNo) {
        return StringUtils.hasText(licenseNo) ? vendor.licenseNo.containsIgnoreCase(licenseNo) : null;
    }

    private BooleanExpression containsCompName(String compName) {
        return StringUtils.hasText(compName) ? vendor.compName.containsIgnoreCase(compName) : null;
    }

    private BooleanExpression containsCeoName(String ceoName) {
        return StringUtils.hasText(ceoName) ? vendor.ceoName.containsIgnoreCase(ceoName) : null;
    }

    private BooleanExpression containsCompEmail(String compEmail) {
        return StringUtils.hasText(compEmail) ? vendor.compEmail.containsIgnoreCase(compEmail) : null;
    }

    private BooleanExpression containsTelNumber(String telNumber) {
        return StringUtils.hasText(telNumber) ? vendor.telNumber.containsIgnoreCase(telNumber) : null;
    }

    private BooleanExpression containsFaxNumber(String faxNumber) {
        return StringUtils.hasText(faxNumber) ? vendor.faxNumber.containsIgnoreCase(faxNumber) : null;
    }

    private BooleanExpression containsZipCode(String zipCode) {
        return StringUtils.hasText(zipCode) ? vendor.zipCode.containsIgnoreCase(zipCode) : null;
    }

    private BooleanExpression containsAddressDtl(String addressDtl) {
        return StringUtils.hasText(addressDtl) ? vendor.addressDtl.containsIgnoreCase(addressDtl) : null;
    }

    private BooleanExpression containsAddressMst(String addressMst) {
        return StringUtils.hasText(addressMst) ? vendor.addressMst.containsIgnoreCase(addressMst) : null;
    }
}