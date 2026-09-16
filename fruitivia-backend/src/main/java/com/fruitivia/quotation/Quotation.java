package com.fruitivia.quotation;

import com.fruitivia.buyer.Buyer;
import com.fruitivia.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuotationStatus status;

    @Column(name = "currency_code", length = 3)
    private String currencyCode;

    @Column(name = "packaging_cost", precision = 19, scale = 4)
    private BigDecimal packagingCost;

    @Column(name = "transportation_cost", precision = 19, scale = 4)
    private BigDecimal transportationCost;

    @Column(name = "export_handling_cost", precision = 19, scale = 4)
    private BigDecimal exportHandlingCost;

    @Column(name = "shipping_cost", precision = 19, scale = 4)
    private BigDecimal shippingCost;

    @Column(name = "insurance_cost", precision = 19, scale = 4)
    private BigDecimal insuranceCost;

    @Column(precision = 19, scale = 4)
    private BigDecimal discount;

    @Column(name = "tax_amount", precision = 19, scale = 4)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "payment_terms", columnDefinition = "TEXT")
    private String paymentTerms;

    @Column(name = "validity_date")
    private OffsetDateTime validityDate;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuotationItem> items = new ArrayList<>();

    public void addItem(QuotationItem item) {
        items.add(item);
        item.setQuotation(this);
    }

    public void removeItem(QuotationItem item) {
        items.remove(item);
        item.setQuotation(null);
    }
}
