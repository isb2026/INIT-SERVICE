package com.lts5.init.repository.progressvendor;

import com.lts5.init.entity.ProgressVendor;
import com.lts5.init.payload.request.progressvendor.ProgressVendorSearchRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProgressVendorRepositoryImpl implements ProgressVendorRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<ProgressVendor> searchProgressVendors(ProgressVendorSearchRequest request, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProgressVendor> query = cb.createQuery(ProgressVendor.class);
        Root<ProgressVendor> progressVendor = query.from(ProgressVendor.class);
        
        List<Predicate> predicates = new ArrayList<>();

        if (request.getProgressId() != null) {
            predicates.add(cb.equal(progressVendor.get("progressId"), request.getProgressId()));
        }

        if (request.getVendorId() != null) {
            predicates.add(cb.equal(progressVendor.get("vendorId"), request.getVendorId()));
        }

        if (request.getMinUnitCost() != null) {
            predicates.add(cb.greaterThanOrEqualTo(progressVendor.get("unitCost"), request.getMinUnitCost()));
        }

        if (request.getMaxUnitCost() != null) {
            predicates.add(cb.lessThanOrEqualTo(progressVendor.get("unitCost"), request.getMaxUnitCost()));
        }

        if (request.getUnit() != null && !request.getUnit().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(progressVendor.get("unit")), "%" + request.getUnit().toLowerCase() + "%"));
        }

        if (request.getIsDefaultVendor() != null) {
            predicates.add(cb.equal(progressVendor.get("isDefaultVendor"), request.getIsDefaultVendor()));
        }

        if (request.getCreateBy() != null && !request.getCreateBy().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(progressVendor.get("createBy")), "%" + request.getCreateBy().toLowerCase() + "%"));
        }

        if (request.getUpdateBy() != null && !request.getUpdateBy().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(progressVendor.get("updateBy")), "%" + request.getUpdateBy().toLowerCase() + "%"));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(progressVendor.get("progressId")), cb.asc(progressVendor.get("vendorId")));

        // Count query for total elements
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ProgressVendor> countRoot = countQuery.from(ProgressVendor.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        // Main query with pagination
        TypedQuery<ProgressVendor> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        
        List<ProgressVendor> content = typedQuery.getResultList();

        return new PageImpl<>(content, pageable, total);
    }
} 