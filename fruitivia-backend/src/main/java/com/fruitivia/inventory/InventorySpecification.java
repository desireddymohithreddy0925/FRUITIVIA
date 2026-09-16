package com.fruitivia.inventory;

import com.fruitivia.common.specification.SpecificationUtils;
import com.fruitivia.inventory.dto.InventoryFilter;
import com.fruitivia.quality.QualityGrade;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class InventorySpecification {

    public static Specification<Inventory> build(InventoryFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Specification<Inventory>> specs = new ArrayList<>();

            if (filter.getMinAvailableQuantity() != null) {
                specs.add(SpecificationUtils.greaterThanOrEqualTo("availableQuantity", filter.getMinAvailableQuantity()));
            }

            if (filter.getMaxAvailableQuantity() != null) {
                specs.add(SpecificationUtils.lessThanOrEqualTo("availableQuantity", filter.getMaxAvailableQuantity()));
            }

            if (filter.getMinReservedQuantity() != null) {
                specs.add(SpecificationUtils.greaterThanOrEqualTo("reservedQuantity", filter.getMinReservedQuantity()));
            }

            if (filter.getMaxReservedQuantity() != null) {
                specs.add(SpecificationUtils.lessThanOrEqualTo("reservedQuantity", filter.getMaxReservedQuantity()));
            }

            if (filter.getExpiryDateFrom() != null) {
                specs.add((r, q, c) -> c.greaterThanOrEqualTo(r.join("batch").get("expirationDate"), filter.getExpiryDateFrom()));
            }

            if (filter.getExpiryDateTo() != null) {
                specs.add((r, q, c) -> c.lessThanOrEqualTo(r.join("batch").get("expirationDate"), filter.getExpiryDateTo()));
            }

            if (filter.getWarehouseId() != null) {
                specs.add(SpecificationUtils.equalJoin("warehouse", "id", filter.getWarehouseId()));
            }

            if (filter.getQualityGrade() != null) {
                try {
                    QualityGrade grade = QualityGrade.valueOf(filter.getQualityGrade());
                    specs.add((r, q, c) -> c.equal(r.join("batch").get("qualityGrade"), grade));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid enum
                }
            }

            Specification<Inventory> result = null;
            for (Specification<Inventory> spec : specs) {
                result = (result == null) ? Specification.where(spec) : result.and(spec);
            }

            return result == null ? cb.conjunction() : result.toPredicate(root, query, cb);
        };
    }
}
