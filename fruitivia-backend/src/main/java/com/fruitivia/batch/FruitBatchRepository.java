package com.fruitivia.batch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FruitBatchRepository extends JpaRepository<FruitBatch, UUID>, JpaSpecificationExecutor<FruitBatch> {
    Optional<FruitBatch> findByBatchNumber(String batchNumber);
    List<FruitBatch> findByStatusAndExpirationDateBefore(BatchStatus status, Instant date);
    List<FruitBatch> findByStatusAndExpirationDateBetween(BatchStatus status, Instant start, Instant end);
}
