package com.fruitivia.batch;

import com.fruitivia.batch.dto.FruitBatchFilter;
import com.fruitivia.common.specification.SpecificationUtils;
import com.fruitivia.quality.QualityGrade;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FruitBatchSpecification {

    public static Specification<FruitBatch> build(FruitBatchFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Specification<FruitBatch>> specs = new ArrayList<>();

            if (filter.getFruitId() != null) {
                specs.add(SpecificationUtils.equalJoin("fruit", "id", filter.getFruitId()));
            }

            if (filter.getVarietyId() != null) {
                specs.add(SpecificationUtils.equalJoin("variety", "id", filter.getVarietyId()));
            }

            if (filter.getQualityGrade() != null) {
                try {
                    QualityGrade grade = QualityGrade.valueOf(filter.getQualityGrade());
                    specs.add(SpecificationUtils.equal("qualityGrade", grade));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid enum
                }
            }

            if (filter.getHarvestDateFrom() != null) {
                specs.add(SpecificationUtils.greaterThanOrEqualTo("harvestDate", filter.getHarvestDateFrom()));
            }

            if (filter.getHarvestDateTo() != null) {
                specs.add(SpecificationUtils.lessThanOrEqualTo("harvestDate", filter.getHarvestDateTo()));
            }

            if (filter.getExpirationDateFrom() != null) {
                specs.add(SpecificationUtils.greaterThanOrEqualTo("expirationDate", filter.getExpirationDateFrom()));
            }

            if (filter.getExpirationDateTo() != null) {
                specs.add(SpecificationUtils.lessThanOrEqualTo("expirationDate", filter.getExpirationDateTo()));
            }

            if (filter.getRemainingShelfLifeDays() != null) {
                Instant targetDate = Instant.now().plus(filter.getRemainingShelfLifeDays(), ChronoUnit.DAYS);
                specs.add(SpecificationUtils.greaterThanOrEqualTo("expirationDate", targetDate));
            }

            if (filter.getWarehouseId() != null) {
                specs.add(SpecificationUtils.equalJoin("warehouse", "id", filter.getWarehouseId()));
            }

            if (filter.getStatus() != null) {
                try {
                    BatchStatus status = BatchStatus.valueOf(filter.getStatus());
                    specs.add(SpecificationUtils.equal("status", status));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid enum
                }
            }

            if (filter.getSupplierId() != null) {
                specs.add(SpecificationUtils.equalJoin("supplier", "id", filter.getSupplierId()));
            }

            Specification<FruitBatch> result = null;
            for (Specification<FruitBatch> spec : specs) {
                result = (result == null) ? Specification.where(spec) : result.and(spec);
            }

            return result == null ? cb.conjunction() : result.toPredicate(root, query, cb);
        };
    }
}
