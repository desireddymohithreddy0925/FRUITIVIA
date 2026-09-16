package com.fruitivia.packaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PackagingStatusHistoryRepository extends JpaRepository<PackagingStatusHistory, UUID> {
}
