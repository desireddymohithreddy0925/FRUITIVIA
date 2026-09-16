package com.fruitivia.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExportDocumentRepository extends JpaRepository<ExportDocument, UUID> {
    List<ExportDocument> findByOrderId(UUID orderId);
}
