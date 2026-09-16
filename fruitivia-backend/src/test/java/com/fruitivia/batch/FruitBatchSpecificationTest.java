package com.fruitivia.batch;

import com.fruitivia.batch.dto.FruitBatchFilter;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FruitBatchSpecificationTest {

    @Test
    void build_WithNullFilter_ReturnsConjunction() {
        Specification<FruitBatch> spec = FruitBatchSpecification.build(null);
        assertNotNull(spec);
    }

    @Test
    void build_WithFilters_ReturnsSpecification() {
        FruitBatchFilter filter = FruitBatchFilter.builder()
                .fruitId(UUID.randomUUID())
                .qualityGrade("A")
                .warehouseId(UUID.randomUUID())
                .status("CREATED")
                .build();
        Specification<FruitBatch> spec = FruitBatchSpecification.build(filter);
        assertNotNull(spec);
    }
}
