package com.fruitivia.quotation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuotationStatusHistoryRepository extends JpaRepository<QuotationStatusHistory, UUID> {
}
