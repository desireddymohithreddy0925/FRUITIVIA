package com.fruitivia.jobs;

import com.fruitivia.quotation.Quotation;
import com.fruitivia.quotation.QuotationRepository;
import com.fruitivia.quotation.QuotationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotationJobServiceTest {

    @Mock
    private QuotationRepository quotationRepository;

    @InjectMocks
    private QuotationJobService quotationJobService;

    @Test
    void expireQuotations_ChangesStatusToExpired() {
        Quotation quotation = new Quotation();
        quotation.setId(UUID.randomUUID());
        quotation.setStatus(QuotationStatus.SENT);

        when(quotationRepository.findByStatusAndValidityDateBefore(eq(QuotationStatus.SENT), any(Instant.class)))
                .thenReturn(List.of(quotation));

        quotationJobService.expireQuotations();

        assertThat(quotation.getStatus()).isEqualTo(QuotationStatus.EXPIRED);
        verify(quotationRepository).saveAll(List.of(quotation));
    }
}
