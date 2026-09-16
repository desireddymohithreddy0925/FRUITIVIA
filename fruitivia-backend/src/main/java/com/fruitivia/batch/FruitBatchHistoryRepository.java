package com.fruitivia.batch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FruitBatchHistoryRepository extends JpaRepository<FruitBatchHistory, UUID> {
}
