package com.fruitivia.packaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PackagingRecordRepository extends JpaRepository<PackagingRecord, UUID> {
    List<PackagingRecord> findByOrderId(UUID orderId);
}
