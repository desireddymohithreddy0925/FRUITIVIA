package com.fruitivia.quotation;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class QuotationSpecification {

    public static Specification<Quotation> hasBuyerId(UUID buyerId) {
        return (root, query, cb) -> {
            if (buyerId == null) {
                return null;
            }
            return cb.equal(root.get("buyer").get("id"), buyerId);
        };
    }

    public static Specification<Quotation> hasStatus(QuotationStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }
}
