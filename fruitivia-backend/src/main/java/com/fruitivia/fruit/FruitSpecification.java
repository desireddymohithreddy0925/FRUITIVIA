package com.fruitivia.fruit;

import com.fruitivia.common.specification.SpecificationUtils;
import com.fruitivia.fruit.dto.FruitFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FruitSpecification {

    public static Specification<Fruit> build(FruitFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Specification<Fruit>> specs = new ArrayList<>();

            if (filter.getName() != null) {
                specs.add(SpecificationUtils.<Fruit>likeIgnoreCase("name", filter.getName()));
            }

            if (filter.getVarieties() != null && !filter.getVarieties().isEmpty()) {
                specs.add(SpecificationUtils.in("varieties", filter.getVarieties())); // Assuming varieties is a collection or requires join
                // Note: varieties in Fruit is a OneToMany List<FruitVariety>. 
                // So filtering by variety name would require a join.
            }

            if (filter.getOrigin() != null) {
                specs.add(SpecificationUtils.<Fruit>likeIgnoreCase("origin", filter.getOrigin()));
            }

            if (filter.getCategory() != null) {
                specs.add(SpecificationUtils.<Fruit>likeIgnoreCase("category", filter.getCategory()));
            }

            if (filter.getSeason() != null) {
                specs.add(SpecificationUtils.<Fruit>likeIgnoreCase("season", filter.getSeason()));
            }

            Specification<Fruit> result = null;
            for (Specification<Fruit> spec : specs) {
                result = (result == null) ? Specification.where(spec) : result.and(spec);
            }

            return result == null ? cb.conjunction() : result.toPredicate(root, query, cb);
        };
    }
}
