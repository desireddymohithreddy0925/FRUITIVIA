package com.fruitivia.order;

import com.fruitivia.inventory.InventoryService;
import com.fruitivia.inventory.dto.InventoryAllocationResult;
import com.fruitivia.common.event.EventPublisher;
import com.fruitivia.order.event.CustomsClearedEvent;
import com.fruitivia.common.event.shipment.ShipmentDeliveredEvent;
import com.fruitivia.common.event.shipment.ShipmentDispatchedEvent;
import com.fruitivia.user.Role;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fruitivia.customs.CustomsClearanceRepository;
import com.fruitivia.customs.CustomsStatus;
import com.fruitivia.customs.CustomsClearance;

@Service
@RequiredArgsConstructor
public class OrderStateMachineService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final OrderItemAllocationRepository allocationRepository;
    private final EventPublisher eventPublisher;
    private final CustomsClearanceRepository customsClearanceRepository;

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        VALID_TRANSITIONS.put(OrderStatus.QUOTE_ACCEPTED, EnumSet.of(OrderStatus.PAYMENT_PENDING, OrderStatus.CANCELLED));
        VALID_TRANSITIONS.put(OrderStatus.PAYMENT_PENDING, EnumSet.of(OrderStatus.PAID, OrderStatus.CANCELLED));
        VALID_TRANSITIONS.put(OrderStatus.PAID, EnumSet.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED));
        VALID_TRANSITIONS.put(OrderStatus.PROCESSING, EnumSet.of(OrderStatus.PACKED, OrderStatus.CANCELLED));
        VALID_TRANSITIONS.put(OrderStatus.PACKED, EnumSet.of(OrderStatus.DISPATCHED));
        VALID_TRANSITIONS.put(OrderStatus.DISPATCHED, EnumSet.of(OrderStatus.IN_TRANSIT));
        VALID_TRANSITIONS.put(OrderStatus.IN_TRANSIT, EnumSet.of(OrderStatus.CUSTOMS, OrderStatus.DELIVERED));
        VALID_TRANSITIONS.put(OrderStatus.CUSTOMS, EnumSet.of(OrderStatus.DELIVERED));
        VALID_TRANSITIONS.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
        VALID_TRANSITIONS.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }

    @Transactional
    public Order transitionState(UUID orderId, OrderStatus targetStatus, String notes) {
        User actor = getCurrentUser();
        return doTransition(orderId, targetStatus, notes, actor, false);
    }

    @Transactional
    public Order systemTransitionState(UUID orderId, OrderStatus targetStatus, String notes) {
        return doTransition(orderId, targetStatus, notes, null, true);
    }

    private Order doTransition(UUID orderId, OrderStatus targetStatus, String notes, User actor, boolean isSystemAction) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        if (!isValidTransition(currentStatus, targetStatus)) {
            throw new IllegalStateException(String.format("Invalid state transition from %s to %s", currentStatus, targetStatus));
        }

        if (!isSystemAction) {
            verifyAuthorization(actor, targetStatus);
        }
        
        executePreconditions(order, targetStatus);

        order.setStatus(targetStatus);
        orderRepository.save(order);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(targetStatus)
                .actor(actor)
                .notes(notes)
                .build();
        history.setActive(true);
        historyRepository.save(history);

        executePostConditions(order, targetStatus);

        return order;
    }

    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        Set<OrderStatus> allowed = VALID_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    private void verifyAuthorization(User actor, OrderStatus targetStatus) {
        if (actor == null) return; // System action

        Role role = actor.getRole();
        
        switch (targetStatus) {
            case PAYMENT_PENDING:
            case PAID:
                if (role != Role.ADMIN) {
                    throw new org.springframework.security.access.AccessDeniedException("Only ADMIN can update payment status");
                }
                break;
            case PROCESSING:
                if (role != Role.ADMIN && role != Role.ENGINEER) {
                    throw new org.springframework.security.access.AccessDeniedException("Only ADMIN or ENGINEER can process orders");
                }
                break;
            case PACKED:
            case DISPATCHED:
            case IN_TRANSIT:
            case CUSTOMS:
            case DELIVERED:
                if (role != Role.ADMIN && role != Role.ENGINEER && role != Role.LOGISTICS) {
                    throw new org.springframework.security.access.AccessDeniedException("Unauthorized to update logistics status");
                }
                break;
            case CANCELLED:
                if (role != Role.ADMIN) {
                    throw new org.springframework.security.access.AccessDeniedException("Only ADMIN can cancel orders");
                }
                break;
            default:
                break;
        }
    }

    private void executePreconditions(Order order, OrderStatus targetStatus) {
        if (targetStatus == OrderStatus.PACKED && order.getStatus() != OrderStatus.PROCESSING) {
            // Strictly shouldn't happen due to VALID_TRANSITIONS map, but adding a specific business rule check
            throw new IllegalStateException("Order must be in PROCESSING to become PACKED");
        }
        
        if (targetStatus == OrderStatus.DELIVERED || targetStatus == OrderStatus.IN_TRANSIT) {
            // Check if customs clearance exists and is CLEARED (assuming all international orders need customs)
            // Note: If domestic orders don't need customs, we would add logic to check order.isInternational()
            CustomsClearance clearance = customsClearanceRepository.findByOrderId(order.getId()).orElse(null);
            
            // To prevent breaking existing flows where Customs isn't started yet, 
            // we enforce it if it exists. If the business rule strictly requires it for ALL orders, 
            // we throw if null. Let's assume strict rule: if there's a clearance, it MUST be CLEARED.
            // If the system requires a clearance to even proceed, we throw if null. Let's just check if it exists and is not CLEARED.
            if (clearance != null && clearance.getStatus() != CustomsStatus.CLEARED) {
                throw new IllegalStateException("Cannot deliver/transit order. Customs clearance is not CLEARED. Current status: " + clearance.getStatus());
            }
        }
    }

    private void executePostConditions(Order order, OrderStatus targetStatus) {
        if (targetStatus == OrderStatus.PROCESSING) {
            reserveInventory(order);
        } else if (targetStatus == OrderStatus.CANCELLED) {
            releaseInventory(order);
        } else if (targetStatus == OrderStatus.DISPATCHED) {
            eventPublisher.publish(new ShipmentDispatchedEvent(this, order.getId()));
        } else if (targetStatus == OrderStatus.CUSTOMS) {
            eventPublisher.publish(new CustomsClearedEvent(this, order.getId()));
        } else if (targetStatus == OrderStatus.DELIVERED) {
            eventPublisher.publish(new ShipmentDeliveredEvent(this, order.getId()));
        }
    }

    private void reserveInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            List<InventoryAllocationResult> results = inventoryService.allocateFIFO(
                    item.getFruit().getId(), 
                    item.getVariety().getId(), 
                    item.getQuantity(), 
                    "Order " + order.getOrderNumber(), 
                    order.getId().toString()
            );
            
            for (InventoryAllocationResult result : results) {
                com.fruitivia.batch.FruitBatch batchProxy = new com.fruitivia.batch.FruitBatch();
                batchProxy.setId(result.getBatchId());

                OrderItemAllocation allocation = OrderItemAllocation.builder()
                        .orderItem(item)
                        .fruitBatch(batchProxy)
                        .quantity(result.getAllocatedQuantity())
                        .build();
                allocation.setActive(true);
                allocationRepository.save(allocation);
                
                item.getAllocations().add(allocation);
            }
        }
    }

    private void releaseInventory(Order order) {
        // Implement if required
        // Need to loop over OrderItemAllocations, call inventoryService.release()
    }

    private User getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }
}
