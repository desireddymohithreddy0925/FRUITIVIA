package com.fruitivia.shipment;

import com.fruitivia.shipment.dto.ShipmentFilter;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ShipmentSpecificationTest {

    @Test
    void build_WithNullFilter_ReturnsConjunction() {
        Specification<Shipment> spec = ShipmentSpecification.build(null);
        assertNotNull(spec);
    }

    @Test
    void build_WithFilters_ReturnsSpecification() {
        ShipmentFilter filter = ShipmentFilter.builder()
                .status("PREPARING")
                .country("USA")
                .customsStatus("CLEARED")
                .build();
        Specification<Shipment> spec = ShipmentSpecification.build(filter);
        assertNotNull(spec);
    }
}
