package com.fruitivia.inventory;

import com.fruitivia.inventory.dto.InventoryFilter;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class InventorySpecificationTest {

    @Test
    void build_WithNullFilter_ReturnsConjunction() {
        Specification<Inventory> spec = InventorySpecification.build(null);
        assertNotNull(spec);
    }

    @Test
    void build_WithFilters_ReturnsSpecification() {
        InventoryFilter filter = InventoryFilter.builder()
                .warehouseId(UUID.randomUUID())
                .minAvailableQuantity(new java.math.BigDecimal("10"))
                .build();
        Specification<Inventory> spec = InventorySpecification.build(filter);
        assertNotNull(spec);
    }
}
