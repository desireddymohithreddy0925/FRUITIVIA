package com.fruitivia.quality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QualityInspectionHistoryRepository extends JpaRepository<QualityInspectionHistory, UUID> {
}
