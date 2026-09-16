package com.fruitivia.customs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomsClearanceRepository extends JpaRepository<CustomsClearance, UUID> {
    Optional<CustomsClearance> findByOrderId(UUID orderId);
}
