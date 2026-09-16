package com.fruitivia.order;

import com.fruitivia.order.event.OrderPlacedEvent;
import com.fruitivia.quotation.Quotation;
import com.fruitivia.quotation.QuotationItem;
import com.fruitivia.quotation.QuotationRepository;
import com.fruitivia.quotation.QuotationStatus;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final QuotationRepository quotationRepository;
    private final OrderSequenceService sequenceService;
    private final OrderStatusHistoryRepository historyRepository;
    private final com.fruitivia.common.event.EventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final com.fruitivia.buyer.BuyerRepository buyerRepository;

    @Transactional
    public com.fruitivia.order.dto.OrderDto createOrderFromQuotation(UUID quotationId) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new EntityNotFoundException("Quotation not found"));
                
        // Validation: Must be accepted
        if (quotation.getStatus() != QuotationStatus.ACCEPTED) {
            throw new IllegalStateException("Order can only be created from an ACCEPTED quotation");
        }
        
        // Validation: Prevent duplicate order
        if (orderRepository.existsByQuotationId(quotationId)) {
            throw new IllegalStateException("An order already exists for this quotation");
        }

        // Authorization: Buyer can only create order for their own quotation
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            com.fruitivia.buyer.Buyer buyer = buyerRepository.findByUserId(currentUser.getId()).orElse(null);
            if (buyer != null) {
                if (!quotation.getBuyer().getId().equals(buyer.getId())) {
                    throw new org.springframework.security.access.AccessDeniedException("Unauthorized to create order for this quotation");
                }
            }
        }

        Order order = Order.builder()
                .orderNumber(sequenceService.generateOrderNumber("ORD"))
                .buyer(quotation.getBuyer())
                .quotation(quotation)
                .status(OrderStatus.QUOTE_ACCEPTED)
                .currencyCode(quotation.getCurrencyCode())
                .totalAmount(quotation.getTotalAmount())
                .build();

        for (QuotationItem qItem : quotation.getItems()) {
            OrderItem oItem = OrderItem.builder()
                    .fruit(qItem.getFruit())
                    .variety(qItem.getVariety())
                    .quantity(qItem.getQuantity())
                    .unitPrice(qItem.getUnitPrice())
                    .lineTotal(qItem.getLineTotal())
                    .build();
            order.addItem(oItem);
        }

        order.setActive(true);
        order = orderRepository.save(order);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.QUOTE_ACCEPTED)
                .actor(currentUser)
                .notes("Order created from quotation " + quotation.getId().toString())
                .build();
        history.setActive(true);
        historyRepository.save(history);

        eventPublisher.publish(new OrderPlacedEvent(this, order.getId()));

        return mapToDto(order);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<com.fruitivia.order.dto.OrderDto> getAllOrders(com.fruitivia.order.dto.OrderFilter filter, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<Order> spec = org.springframework.data.jpa.domain.Specification.where(OrderSpecification.build(filter))
                .and((root, query, cb) -> cb.isTrue(root.get("active")));
        return orderRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    public com.fruitivia.order.dto.OrderDto getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
                
        User currentUser = getCurrentUser();
        if (currentUser != null && currentUser.getRole() == com.fruitivia.user.Role.BUYER) {
            com.fruitivia.buyer.Buyer buyer = buyerRepository.findByUserId(currentUser.getId()).orElse(null);
            if (buyer == null || !buyer.getId().equals(order.getBuyer().getId())) {
                throw new org.springframework.security.access.AccessDeniedException("Access denied");
            }
        }
        
        return mapToDto(order);
    }

    private com.fruitivia.order.dto.OrderDto mapToDto(Order order) {
        return com.fruitivia.order.dto.OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .quotationId(order.getQuotation().getId())
                .buyerId(order.getBuyer().getId())
                .status(order.getStatus())
                .currencyCode(order.getCurrencyCode())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(item -> 
                    com.fruitivia.order.dto.OrderItemDto.builder()
                            .id(item.getId())
                            .fruitId(item.getFruit().getId())
                            .fruitName(item.getFruit().getName())
                            .varietyId(item.getVariety().getId())
                            .varietyName(item.getVariety().getName())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .lineTotal(item.getLineTotal())
                            .build()
                ).toList())
                .build();
    }

    private User getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }
}
