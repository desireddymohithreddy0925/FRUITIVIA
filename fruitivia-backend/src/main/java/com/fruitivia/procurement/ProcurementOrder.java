package com.fruitivia.procurement;

import com.fruitivia.common.entity.BaseEntity;
import com.fruitivia.supplier.Supplier;
import com.fruitivia.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "procurement_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcurementOrder extends BaseEntity {

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "purchase_date", nullable = false)
    private Instant purchaseDate;

    @Column(name = "harvest_date")
    private Instant harvestDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProcurementStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String currency;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "procurementOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProcurementItem> items = new ArrayList<>();
}
