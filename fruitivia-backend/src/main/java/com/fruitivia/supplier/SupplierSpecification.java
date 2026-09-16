package com.fruitivia.supplier;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupplierSpecification {

    public static Specification<Supplier> searchByCriteria(Map<String, String> criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.get("name"))) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.get("name").toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.get("supplierCode"))) {
                predicates.add(cb.equal(root.get("supplierCode"), criteria.get("supplierCode")));
            }

            if (StringUtils.hasText(criteria.get("region"))) {
                predicates.add(cb.like(cb.lower(root.get("region")), "%" + criteria.get("region").toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.get("active"))) {
                boolean active = Boolean.parseBoolean(criteria.get("active"));
                predicates.add(cb.equal(root.get("active"), active));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
