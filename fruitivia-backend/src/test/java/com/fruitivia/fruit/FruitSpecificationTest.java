package com.fruitivia.fruit;

import com.fruitivia.fruit.dto.FruitFilter;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FruitSpecificationTest {

    @Test
    void build_WithNullFilter_ReturnsConjunction() {
        Specification<Fruit> spec = FruitSpecification.build(null);
        assertNotNull(spec);
    }

    @Test
    void build_WithFilters_ReturnsSpecification() {
        FruitFilter filter = FruitFilter.builder()
                .name("Apple")
                .category("Pome")
                .origin("USA")
                .season("Autumn")
                .build();
        Specification<Fruit> spec = FruitSpecification.build(filter);
        assertNotNull(spec);
    }
}
