package com.fruitivia.procurement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface ProcurementOrderRepository extends JpaRepository<ProcurementOrder, UUID> {
    Optional<ProcurementOrder> findByOrderNumber(String orderNumber);
}
