package com.fruitivia.shipment;

import com.fruitivia.common.event.EventPublisher;
import com.fruitivia.common.event.shipment.CustomsRequiredEvent;
import com.fruitivia.common.event.shipment.ShipmentDeliveredEvent;
import com.fruitivia.common.event.shipment.ShipmentDispatchedEvent;
import com.fruitivia.customs.CustomsClearance;
import com.fruitivia.customs.CustomsClearanceRepository;
import com.fruitivia.customs.CustomsStatus;
import com.fruitivia.document.DocumentType;
import com.fruitivia.document.ExportDocumentRepository;
import com.fruitivia.order.Order;
import com.fruitivia.order.OrderRepository;
import com.fruitivia.order.OrderStatus;
import com.fruitivia.packaging.PackagingRecord;
import com.fruitivia.packaging.PackagingStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentStateMachineService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentStatusHistoryRepository historyRepository;
    private final OrderRepository orderRepository;
    private final CustomsClearanceRepository customsClearanceRepository;
    private final ExportDocumentRepository exportDocumentRepository;
    private final EventPublisher eventPublisher;

    private static final Map<ShipmentStatus, Set<ShipmentStatus>> VALID_TRANSITIONS = new EnumMap<>(ShipmentStatus.class);

    static {
        VALID_TRANSITIONS.put(ShipmentStatus.PREPARING, EnumSet.of(ShipmentStatus.PACKED, ShipmentStatus.CANCELLED));
        VALID_TRANSITIONS.put(ShipmentStatus.PACKED, EnumSet.of(ShipmentStatus.DISPATCHED, ShipmentStatus.CANCELLED));
        VALID_TRANSITIONS.put(ShipmentStatus.DISPATCHED, EnumSet.of(ShipmentStatus.IN_TRANSIT));
        VALID_TRANSITIONS.put(ShipmentStatus.IN_TRANSIT, EnumSet.of(ShipmentStatus.CUSTOMS, ShipmentStatus.DELIVERED, ShipmentStatus.DELAYED));
        VALID_TRANSITIONS.put(ShipmentStatus.DELAYED, EnumSet.of(ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED));
        VALID_TRANSITIONS.put(ShipmentStatus.CUSTOMS, EnumSet.of(ShipmentStatus.DELIVERED, ShipmentStatus.DELAYED));
        VALID_TRANSITIONS.put(ShipmentStatus.DELIVERED, EnumSet.noneOf(ShipmentStatus.class));
        VALID_TRANSITIONS.put(ShipmentStatus.CANCELLED, EnumSet.noneOf(ShipmentStatus.class));
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Shipment> getAllShipments(com.fruitivia.shipment.dto.ShipmentFilter filter, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<Shipment> spec = org.springframework.data.jpa.domain.Specification.where(ShipmentSpecification.build(filter))
                .and((root, query, cb) -> cb.isTrue(root.get("active")));
        return shipmentRepository.findAll(spec, pageable);
    }

    @Transactional
    public Shipment createShipment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (shipmentRepository.findByOrderId(orderId).isPresent()) {
            throw new IllegalStateException("Shipment already exists for order: " + orderId);
        }

        Shipment shipment = Shipment.builder()
                .order(order)
                .status(ShipmentStatus.PREPARING)
                .build();
        
        shipment = shipmentRepository.save(shipment);
        recordHistory(shipment, ShipmentStatus.PREPARING, "Shipment created");
        return shipment;
    }

    @Transactional
    public Shipment transitionState(UUID shipmentId, ShipmentStatus targetStatus, String notes) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found: " + shipmentId));

        if (!isValidTransition(shipment.getStatus(), targetStatus)) {
            throw new IllegalStateException("Invalid transition from " + shipment.getStatus() + " to " + targetStatus);
        }

        executePreconditions(shipment, targetStatus);

        shipment.setStatus(targetStatus);
        
        if (targetStatus == ShipmentStatus.DISPATCHED) {
            shipment.setShipmentDate(OffsetDateTime.now());
        } else if (targetStatus == ShipmentStatus.DELIVERED) {
            shipment.setActualDelivery(OffsetDateTime.now());
        }

        shipmentRepository.save(shipment);
        recordHistory(shipment, targetStatus, notes);
        
        executePostConditions(shipment, targetStatus);

        return shipment;
    }

    private void executePreconditions(Shipment shipment, ShipmentStatus targetStatus) {
        Order order = shipment.getOrder();
        
        switch (targetStatus) {
            case PACKED:
                if (order.getStatus() != OrderStatus.PROCESSING && order.getStatus() != OrderStatus.PAID) {
                    throw new IllegalStateException("Order payment/requirements not satisfied for packing");
                }
                
                boolean allPacked = shipment.getItems().stream()
                        .map(ShipmentItem::getPackagingRecord)
                        .allMatch(pr -> pr.getStatus() == PackagingStatus.COMPLETED);
                
                if (shipment.getItems().isEmpty() || !allPacked) {
                    throw new IllegalStateException("Packaging must be completed for all items");
                }
                break;
                
            case DISPATCHED:
                boolean hasBL = exportDocumentRepository.findByOrderId(order.getId()).stream()
                        .anyMatch(doc -> doc.getType() == DocumentType.BILL_OF_LADING);
                if (!hasBL) {
                    throw new IllegalStateException("Missing required Bill of Lading");
                }
                break;
                
            case DELIVERED:
                CustomsClearance clearance = customsClearanceRepository.findByOrderId(order.getId()).orElse(null);
                if (clearance != null && clearance.getStatus() != CustomsStatus.CLEARED) {
                    throw new IllegalStateException("Customs clearance required before delivery");
                }
                break;
                
            default:
                break;
        }
    }

    private void executePostConditions(Shipment shipment, ShipmentStatus targetStatus) {
        if (targetStatus == ShipmentStatus.DISPATCHED) {
            eventPublisher.publish(new ShipmentDispatchedEvent(this, shipment.getOrder().getId()));
        } else if (targetStatus == ShipmentStatus.IN_TRANSIT) {
            if (customsClearanceRepository.findByOrderId(shipment.getOrder().getId()).isPresent()) {
                eventPublisher.publish(new CustomsRequiredEvent(this, shipment.getId(), shipment.getOrder().getId()));
            }
        } else if (targetStatus == ShipmentStatus.DELIVERED) {
            eventPublisher.publish(new ShipmentDeliveredEvent(this, shipment.getOrder().getId()));
        }
    }

    private boolean isValidTransition(ShipmentStatus from, ShipmentStatus to) {
        return VALID_TRANSITIONS.getOrDefault(from, EnumSet.noneOf(ShipmentStatus.class)).contains(to);
    }

    private void recordHistory(Shipment shipment, ShipmentStatus status, String notes) {
        String actor = "system";
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            actor = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        ShipmentStatusHistory history = ShipmentStatusHistory.builder()
                .shipment(shipment)
                .status(status)
                .notes(notes)
                .actor(actor)
                .build();
        historyRepository.save(history);
    }
}
