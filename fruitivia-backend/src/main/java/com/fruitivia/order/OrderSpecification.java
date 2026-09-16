package com.fruitivia.order;

import com.fruitivia.common.specification.SpecificationUtils;
import com.fruitivia.order.dto.OrderFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> build(OrderFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Specification<Order>> specs = new ArrayList<>();

            if (filter.getBuyerId() != null) {
                specs.add(SpecificationUtils.equalJoin("buyer", "id", filter.getBuyerId()));
            }

            if (filter.getCountry() != null) {
                specs.add((r, q, c) -> c.like(c.lower(r.join("buyer").get("country")), "%" + filter.getCountry().toLowerCase() + "%"));
            }

            if (filter.getStatus() != null) {
                try {
                    OrderStatus status = OrderStatus.valueOf(filter.getStatus());
                    specs.add(SpecificationUtils.equal("status", status));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid enum
                }
            }

            if (filter.getDateFrom() != null) {
                specs.add(SpecificationUtils.greaterThanOrEqualTo("createdAt", filter.getDateFrom()));
            }

            if (filter.getDateTo() != null) {
                specs.add(SpecificationUtils.lessThanOrEqualTo("createdAt", filter.getDateTo()));
            }

            if (filter.getMinAmount() != null) {
                specs.add(SpecificationUtils.greaterThanOrEqualTo("totalAmount", filter.getMinAmount()));
            }

            if (filter.getMaxAmount() != null) {
                specs.add(SpecificationUtils.lessThanOrEqualTo("totalAmount", filter.getMaxAmount()));
            }

            if (filter.getCurrency() != null) {
                specs.add(SpecificationUtils.<Order>likeIgnoreCase("currencyCode", filter.getCurrency()));
            }

            Specification<Order> result = null;
            for (Specification<Order> spec : specs) {
                result = (result == null) ? Specification.where(spec) : result.and(spec);
            }

            return result == null ? cb.conjunction() : result.toPredicate(root, query, cb);
        };
    }
}
