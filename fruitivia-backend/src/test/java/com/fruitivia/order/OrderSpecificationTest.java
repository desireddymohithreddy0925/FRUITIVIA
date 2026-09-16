package com.fruitivia.order;

import com.fruitivia.order.dto.OrderFilter;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderSpecificationTest {

    @Test
    void build_WithNullFilter_ReturnsConjunction() {
        Specification<Order> spec = OrderSpecification.build(null);
        assertNotNull(spec);
    }

    @Test
    void build_WithFilters_ReturnsSpecification() {
        OrderFilter filter = OrderFilter.builder()
                .buyerId(UUID.randomUUID())
                .status("QUOTE_ACCEPTED")
                .country("USA")
                .build();
        Specification<Order> spec = OrderSpecification.build(filter);
        assertNotNull(spec);
    }
}
