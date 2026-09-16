package com.fruitivia.procurement;

import com.fruitivia.fruit.Fruit;
import com.fruitivia.fruit.FruitRepository;
import com.fruitivia.fruit.FruitVariety;
import com.fruitivia.fruit.FruitVarietyRepository;
import com.fruitivia.procurement.dto.*;
import com.fruitivia.supplier.Supplier;
import com.fruitivia.supplier.SupplierRepository;
import com.fruitivia.warehouse.Warehouse;
import com.fruitivia.warehouse.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcurementService {

    private final ProcurementOrderRepository procurementOrderRepository;
    private final ProcurementStateHistoryRepository stateHistoryRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final FruitRepository fruitRepository;
    private final FruitVarietyRepository varietyRepository;

    @Transactional(readOnly = true)
    public Page<ProcurementOrderDto> getAllOrders(Pageable pageable) {
        return procurementOrderRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public ProcurementOrderDto getOrderById(UUID id) {
        return procurementOrderRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));
    }

    @Transactional
    public ProcurementOrderDto createOrder(ProcurementOrderCreateRequest request, String currentUser) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        String orderNumber = "PO-" + System.currentTimeMillis();
        String currency = StringUtils.hasText(request.getCurrency()) ? request.getCurrency() : "INR";

        ProcurementOrder order = ProcurementOrder.builder()
                .orderNumber(orderNumber)
                .supplier(supplier)
                .purchaseDate(request.getPurchaseDate())
                .harvestDate(request.getHarvestDate())
                .warehouse(warehouse)
                .status(ProcurementStatus.DRAFT)
                .paymentStatus(PaymentStatus.PENDING)
                .currency(currency)
                .notes(request.getNotes())
                .build();
        
        order.setActive(true);

        BigDecimal grandTotal = BigDecimal.ZERO;
        List<ProcurementItem> items = new ArrayList<>();

        for (ProcurementItemCreateRequest itemReq : request.getItems()) {
            Fruit fruit = fruitRepository.findById(itemReq.getFruitId())
                    .orElseThrow(() -> new EntityNotFoundException("Fruit not found"));
            FruitVariety variety = varietyRepository.findById(itemReq.getVarietyId())
                    .orElseThrow(() -> new EntityNotFoundException("Variety not found"));

            if (!variety.getFruit().getId().equals(fruit.getId())) {
                throw new IllegalArgumentException("Variety " + variety.getName() + " does not belong to fruit " + fruit.getName());
            }

            BigDecimal lineTotal = itemReq.getUnitPrice().multiply(itemReq.getQuantity());
            grandTotal = grandTotal.add(lineTotal);

            ProcurementItem item = ProcurementItem.builder()
                    .procurementOrder(order)
                    .fruit(fruit)
                    .variety(variety)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .totalPrice(lineTotal)
                    .build();
            item.setActive(true);
            items.add(item);
        }

        order.setItems(items);
        order.setTotalAmount(grandTotal);

        ProcurementOrder savedOrder = procurementOrderRepository.save(order);

        saveHistory(savedOrder, null, ProcurementStatus.DRAFT, currentUser, "Order created");

        return mapToDto(savedOrder);
    }

    @Transactional
    public ProcurementOrderDto updateStatus(UUID id, ProcurementStatusUpdateRequest request, String currentUser) {
        ProcurementOrder order = procurementOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        ProcurementStatus oldStatus = order.getStatus();
        ProcurementStatus newStatus = request.getStatus();

        if (oldStatus == newStatus) {
            return mapToDto(order);
        }

        // Validate state transitions
        if (!isValidTransition(oldStatus, newStatus)) {
            throw new IllegalArgumentException("Invalid state transition from " + oldStatus + " to " + newStatus);
        }

        order.setStatus(newStatus);
        ProcurementOrder savedOrder = procurementOrderRepository.save(order);

        saveHistory(savedOrder, oldStatus, newStatus, currentUser, request.getComments());

        return mapToDto(savedOrder);
    }

    private boolean isValidTransition(ProcurementStatus current, ProcurementStatus next) {
        return switch (current) {
            case DRAFT -> next == ProcurementStatus.SUBMITTED || next == ProcurementStatus.CANCELLED;
            case SUBMITTED -> next == ProcurementStatus.APPROVED || next == ProcurementStatus.REJECTED || next == ProcurementStatus.CANCELLED;
            case APPROVED -> next == ProcurementStatus.RECEIVED || next == ProcurementStatus.CANCELLED;
            case REJECTED, RECEIVED, CANCELLED -> false; // Terminal states
        };
    }

    private void saveHistory(ProcurementOrder order, ProcurementStatus prev, ProcurementStatus current, String user, String comments) {
        ProcurementStateHistory history = ProcurementStateHistory.builder()
                .procurementOrder(order)
                .previousStatus(prev)
                .newStatus(current)
                .changedBy(user)
                .comments(comments)
                .build();
        history.setActive(true);
        stateHistoryRepository.save(history);
    }

    private ProcurementOrderDto mapToDto(ProcurementOrder order) {
        List<ProcurementItemDto> itemDtos = order.getItems().stream()
                .map(item -> ProcurementItemDto.builder()
                        .id(item.getId())
                        .fruitId(item.getFruit().getId())
                        .fruitName(item.getFruit().getName())
                        .varietyId(item.getVariety().getId())
                        .varietyName(item.getVariety().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return ProcurementOrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .supplierId(order.getSupplier().getId())
                .supplierName(order.getSupplier().getName())
                .purchaseDate(order.getPurchaseDate())
                .harvestDate(order.getHarvestDate())
                .warehouseId(order.getWarehouse().getId())
                .warehouseName(order.getWarehouse().getName())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemDtos)
                .build();
    }
}
