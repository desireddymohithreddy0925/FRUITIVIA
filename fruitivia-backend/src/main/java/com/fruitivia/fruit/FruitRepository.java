package com.fruitivia.fruit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FruitRepository extends JpaRepository<Fruit, UUID>, JpaSpecificationExecutor<Fruit> {
    Optional<Fruit> findByName(String name);
}
