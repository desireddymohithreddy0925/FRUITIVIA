package com.fruitivia.coldchain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ColdChainEventRepository extends JpaRepository<ColdChainEvent, UUID> {
    List<ColdChainEvent> findByBatchId(UUID batchId);
}
