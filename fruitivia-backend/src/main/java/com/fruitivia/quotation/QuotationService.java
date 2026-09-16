package com.fruitivia.quotation;

import com.fruitivia.buyer.Buyer;
import com.fruitivia.buyer.BuyerRepository;
import com.fruitivia.fruit.Fruit;
import com.fruitivia.fruit.FruitVariety;
import com.fruitivia.fruit.FruitRepository;
import com.fruitivia.fruit.FruitVarietyRepository;
import com.fruitivia.quotation.dto.AdminQuotationUpdate;
import com.fruitivia.quotation.dto.QuotationDto;
import com.fruitivia.quotation.dto.QuotationItemDto;
import com.fruitivia.quotation.dto.QuotationItemRequest;
import com.fruitivia.quotation.dto.QuotationRequest;
import com.fruitivia.quotation.event.QuotationAcceptedEvent;
import com.fruitivia.common.event.EventPublisher;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationStatusHistoryRepository historyRepository;
    private final BuyerRepository buyerRepository;
    private final FruitRepository fruitRepository;
    private final FruitVarietyRepository varietyRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public QuotationDto requestQuotation(QuotationRequest request) {
        User currentUser = getCurrentUser();
        Buyer buyer = buyerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Buyer profile not found for current user"));

        Quotation quotation = Quotation.builder()
                .buyer(buyer)
                .status(QuotationStatus.REQUESTED)
                .build();

        for (QuotationItemRequest itemReq : request.getItems()) {
            Fruit fruit = fruitRepository.findById(itemReq.getFruitId())
                    .orElseThrow(() -> new EntityNotFoundException("Fruit not found"));
            FruitVariety variety = varietyRepository.findById(itemReq.getVarietyId())
                    .orElseThrow(() -> new EntityNotFoundException("Variety not found"));

            if (!variety.getFruit().getId().equals(fruit.getId())) {
                throw new IllegalArgumentException("Variety does not belong to the specified fruit");
            }

            QuotationItem item = QuotationItem.builder()
                    .fruit(fruit)
                    .variety(variety)
                    .quantity(itemReq.getQuantity())
                    .build();
            quotation.addItem(item);
        }

        quotation = quotationRepository.save(quotation);
        recordHistory(quotation, QuotationStatus.REQUESTED, currentUser, "Quotation requested by buyer");

        return mapToDto(quotation);
    }

    @Transactional(readOnly = true)
    public Page<QuotationDto> getMyQuotations(QuotationStatus status, Pageable pageable) {
        User currentUser = getCurrentUser();
        Buyer buyer = buyerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Buyer profile not found for current user"));

        Specification<Quotation> spec = Specification.where(QuotationSpecification.hasBuyerId(buyer.getId()));
        if (status != null) {
            spec = spec.and(QuotationSpecification.hasStatus(status));
        }

        return quotationRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public QuotationDto getMyQuotation(UUID id) {
        Quotation quotation = getBuyerQuotation(id);
        return mapToDto(quotation);
    }

    @Transactional
    public QuotationDto updateAndSendQuotation(UUID id, AdminQuotationUpdate update) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quotation not found"));

        if (quotation.getStatus() != QuotationStatus.REQUESTED) {
            throw new IllegalStateException("Only REQUESTED quotations can be updated and sent");
        }

        quotation.setCurrencyCode(update.getCurrencyCode());
        quotation.setPackagingCost(update.getPackagingCost());
        quotation.setTransportationCost(update.getTransportationCost());
        quotation.setExportHandlingCost(update.getExportHandlingCost());
        quotation.setShippingCost(update.getShippingCost());
        quotation.setInsuranceCost(update.getInsuranceCost());
        quotation.setDiscount(update.getDiscount());
        quotation.setTaxAmount(update.getTaxAmount());
        quotation.setPaymentTerms(update.getPaymentTerms());
        quotation.setValidityDate(update.getValidityDate());

        BigDecimal itemsTotal = BigDecimal.ZERO;

        for (QuotationItem item : quotation.getItems()) {
            BigDecimal unitPrice = update.getItemPrices().get(item.getId());
            if (unitPrice == null) {
                throw new IllegalArgumentException("Missing price for item: " + item.getId());
            }
            item.setUnitPrice(unitPrice);
            BigDecimal lineTotal = unitPrice.multiply(item.getQuantity());
            item.setLineTotal(lineTotal);
            itemsTotal = itemsTotal.add(lineTotal);
        }

        // Calculate total amount
        BigDecimal total = itemsTotal
                .add(update.getPackagingCost())
                .add(update.getTransportationCost())
                .add(update.getExportHandlingCost())
                .add(update.getShippingCost())
                .add(update.getInsuranceCost())
                .add(update.getTaxAmount())
                .subtract(update.getDiscount());
        
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        
        quotation.setTotalAmount(total);
        quotation.setStatus(QuotationStatus.SENT);

        quotation = quotationRepository.save(quotation);
        recordHistory(quotation, QuotationStatus.SENT, getCurrentUser(), "Quotation priced and sent to buyer");

        return mapToDto(quotation);
    }

    @Transactional
    public QuotationDto acceptQuotation(UUID id) {
        Quotation quotation = getBuyerQuotation(id);
        
        if (quotation.getStatus() != QuotationStatus.SENT) {
            throw new IllegalStateException("Only SENT quotations can be accepted");
        }
        
        if (quotation.getValidityDate() != null && quotation.getValidityDate().isBefore(OffsetDateTime.now())) {
            throw new IllegalStateException("Quotation has expired");
        }

        quotation.setStatus(QuotationStatus.ACCEPTED);
        quotation = quotationRepository.save(quotation);
        recordHistory(quotation, QuotationStatus.ACCEPTED, getCurrentUser(), "Quotation accepted by buyer");

        eventPublisher.publish(new QuotationAcceptedEvent(this, quotation.getId()));

        return mapToDto(quotation);
    }

    @Transactional
    public QuotationDto rejectQuotation(UUID id) {
        Quotation quotation = getBuyerQuotation(id);
        
        if (quotation.getStatus() != QuotationStatus.SENT && quotation.getStatus() != QuotationStatus.REQUESTED) {
            throw new IllegalStateException("Cannot reject quotation in current status");
        }

        quotation.setStatus(QuotationStatus.REJECTED);
        quotation = quotationRepository.save(quotation);
        recordHistory(quotation, QuotationStatus.REJECTED, getCurrentUser(), "Quotation rejected by buyer");

        return mapToDto(quotation);
    }

    @Transactional
    public void expireQuotations() {
        List<Quotation> expired = quotationRepository.findAll((root, query, cb) -> 
            cb.and(
                cb.equal(root.get("status"), QuotationStatus.SENT),
                cb.lessThan(root.get("validityDate"), OffsetDateTime.now())
            )
        );
        
        User systemUser = getCurrentUser(); // For a real scheduled job, might need a system actor
        
        for (Quotation q : expired) {
            q.setStatus(QuotationStatus.EXPIRED);
            quotationRepository.save(q);
            recordHistory(q, QuotationStatus.EXPIRED, systemUser, "Automatically expired");
        }
    }

    private Quotation getBuyerQuotation(UUID id) {
        User currentUser = getCurrentUser();
        Buyer buyer = buyerRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Buyer profile not found for current user"));

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quotation not found"));

        if (!quotation.getBuyer().getId().equals(buyer.getId())) {
            throw new EntityNotFoundException("Quotation not found"); // Mask IDOR
        }
        return quotation;
    }

    private void recordHistory(Quotation quotation, QuotationStatus status, User actor, String notes) {
        QuotationStatusHistory history = QuotationStatusHistory.builder()
                .quotation(quotation)
                .status(status)
                .actor(actor)
                .notes(notes)
                .build();
        historyRepository.save(history);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private QuotationDto mapToDto(Quotation quotation) {
        return QuotationDto.builder()
                .id(quotation.getId())
                .buyerId(quotation.getBuyer().getId())
                .status(quotation.getStatus())
                .currencyCode(quotation.getCurrencyCode())
                .packagingCost(quotation.getPackagingCost())
                .transportationCost(quotation.getTransportationCost())
                .exportHandlingCost(quotation.getExportHandlingCost())
                .shippingCost(quotation.getShippingCost())
                .insuranceCost(quotation.getInsuranceCost())
                .discount(quotation.getDiscount())
                .taxAmount(quotation.getTaxAmount())
                .totalAmount(quotation.getTotalAmount())
                .paymentTerms(quotation.getPaymentTerms())
                .validityDate(quotation.getValidityDate())
                .createdAt(quotation.getCreatedAt())
                .updatedAt(quotation.getUpdatedAt())
                .items(quotation.getItems().stream().map(this::mapItemToDto).collect(Collectors.toList()))
                .build();
    }

    private QuotationItemDto mapItemToDto(QuotationItem item) {
        return QuotationItemDto.builder()
                .id(item.getId())
                .fruitId(item.getFruit().getId())
                .fruitName(item.getFruit().getName())
                .varietyId(item.getVariety().getId())
                .varietyName(item.getVariety().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }
}
