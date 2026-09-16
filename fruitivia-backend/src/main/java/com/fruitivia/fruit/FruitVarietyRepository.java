package com.fruitivia.fruit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import java.util.List;

@Repository
public interface FruitVarietyRepository extends JpaRepository<FruitVariety, UUID> {
    List<FruitVariety> findByFruitId(UUID fruitId);
}
