package com.fruitivia.buyer.catalog;

import com.fruitivia.fruit.Fruit;
import com.fruitivia.fruit.FruitRepository;
import com.fruitivia.fruit.FruitVariety;
import com.fruitivia.fruit.FruitVarietyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final FruitRepository fruitRepository;
    private final FruitVarietyRepository varietyRepository;

    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "catalog", key = "{#filter, #pageable}")
    public org.springframework.data.domain.Page<CatalogFruitDto> getAvailableFruits(com.fruitivia.fruit.dto.FruitFilter filter, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<Fruit> spec = org.springframework.data.jpa.domain.Specification.where(com.fruitivia.fruit.FruitSpecification.build(filter))
                .and((root, query, cb) -> cb.isTrue(root.get("active")));
                
        org.springframework.data.domain.Page<Fruit> fruitPage = fruitRepository.findAll(spec, pageable);

        return fruitPage.map(fruit -> {
            List<FruitVariety> varieties = varietyRepository.findByFruitId(fruit.getId()).stream()
                    .filter(FruitVariety::isActive)
                    .collect(Collectors.toList());
            return new CatalogFruitDto(
                    fruit.getId(),
                    fruit.getName(),
                    fruit.getDescription(),
                    varieties.stream().map(v -> new CatalogVarietyDto(
                            v.getId(),
                            v.getName(),
                            v.getShelfLifeDays(),
                            v.getMinStorageTemperature(),
                            v.getMaxStorageTemperature()
                    )).collect(Collectors.toList())
            );
        });
    }
}
