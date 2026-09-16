package com.fruitivia.jobs;

import com.fruitivia.quotation.Quotation;
import com.fruitivia.quotation.QuotationRepository;
import com.fruitivia.quotation.QuotationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuotationJobService {

    private final QuotationRepository quotationRepository;

    @Scheduled(cron = "0 */10 * * * *") // Every 10 minutes
    @Transactional
    public void expireQuotations() {
        log.info("Running job to expire quotations...");
        List<Quotation> expiredQuotations = quotationRepository.findByStatusAndValidityDateBefore(
                QuotationStatus.SENT, Instant.now()
        );

        for (Quotation quotation : expiredQuotations) {
            quotation.setStatus(QuotationStatus.EXPIRED);
            // Ideally a State Machine transition could be called here if quotation was integrated with one,
            // but for quotation we are directly setting status per phase 13 rules.
            log.info("Quotation {} has expired.", quotation.getId());
        }

        quotationRepository.saveAll(expiredQuotations);
        log.info("Expired {} quotations.", expiredQuotations.size());
    }
}
