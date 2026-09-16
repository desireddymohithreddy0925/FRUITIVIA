package com.fruitivia.shipment;

import com.fruitivia.common.entity.BaseEntity;
import com.fruitivia.order.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.envers.Audited;

@Entity
@Table(name = "shipments")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    @Column(name = "container_number")
    private String containerNumber;

    @Column(name = "shipping_company")
    private String shippingCompany;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "origin_port")
    private String originPort;

    @Column(name = "destination_port")
    private String destinationPort;

    @Column(name = "destination_country", length = 2)
    private String destinationCountry;

    @Column(name = "shipment_date")
    private OffsetDateTime shipmentDate;

    @Column(name = "expected_delivery")
    private OffsetDateTime expectedDelivery;

    @Column(name = "actual_delivery")
    private OffsetDateTime actualDelivery;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ShipmentItem> items = new ArrayList<>();
}
