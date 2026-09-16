package com.fruitivia.quality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface QualityInspectionRepository extends JpaRepository<QualityInspection, UUID> {
    Optional<QualityInspection> findByInspectionNumber(String inspectionNumber);
}
