package com.fruitivia.inventory;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID>, JpaSpecificationExecutor<Inventory> {
    
    Optional<Inventory> findByBatchId(UUID batchId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.id = :id")
    Optional<Inventory> findByIdWithPessimisticWriteLock(@Param("id") UUID id);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.batch.id = :batchId")
    Optional<Inventory> findByBatchIdWithPessimisticWriteLock(@Param("batchId") UUID batchId);

    @Query("SELECT i FROM Inventory i WHERE i.batch.fruit.id = :fruitId AND i.batch.variety.id = :varietyId AND i.availableQuantity > 0 AND i.active = true ORDER BY i.batch.expirationDate ASC NULLS LAST, i.createdAt ASC")
    List<Inventory> findAvailableInventoryByFruitAndVariety(@Param("fruitId") UUID fruitId, @Param("varietyId") UUID varietyId);

    List<Inventory> findByAvailableQuantityLessThanAndActiveTrue(java.math.BigDecimal quantity);
}
