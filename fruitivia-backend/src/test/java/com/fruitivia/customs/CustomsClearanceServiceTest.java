package com.fruitivia.customs;

import com.fruitivia.customs.rules.ComplianceEngine;
import com.fruitivia.order.Order;
import com.fruitivia.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomsClearanceServiceTest {

    @Mock
    private CustomsClearanceRepository clearanceRepository;
    
    @Mock
    private CustomsDeclarationRepository declarationRepository;
    
    @Mock
    private CustomsStatusHistoryRepository historyRepository;
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private ComplianceEngine complianceEngine;

    @InjectMocks
    private CustomsClearanceService clearanceService;

    private Order order;
    private UUID orderId = UUID.randomUUID();
    private UUID clearanceId = UUID.randomUUID();
    private CustomsClearance clearance;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(orderId);

        clearance = new CustomsClearance();
        clearance.setId(clearanceId);
        clearance.setOrder(order);
        clearance.setStatus(CustomsStatus.NOT_STARTED);
    }

    @Test
    void testInitiateClearance() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(clearanceRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(clearanceRepository.save(any(CustomsClearance.class))).thenAnswer(i -> i.getArgument(0));

        CustomsClearance result = clearanceService.initiateClearance(orderId);
        
        assertNotNull(result);
        assertEquals(CustomsStatus.NOT_STARTED, result.getStatus());
        verify(historyRepository, times(1)).save(any(CustomsStatusHistory.class));
    }

    @Test
    void testSubmitDeclaration_Success() {
        when(clearanceRepository.findById(clearanceId)).thenReturn(Optional.of(clearance));
        when(complianceEngine.validateClearance(clearance)).thenReturn(Collections.emptyList());
        when(declarationRepository.save(any(CustomsDeclaration.class))).thenAnswer(i -> i.getArgument(0));
        when(clearanceRepository.save(any(CustomsClearance.class))).thenAnswer(i -> i.getArgument(0));

        CustomsDeclaration declaration = new CustomsDeclaration();
        declaration.setReference("DEC123");

        CustomsClearance result = clearanceService.submitDeclaration(clearanceId, declaration);

        assertEquals(CustomsStatus.SUBMITTED, result.getStatus());
        assertNotNull(result.getDeclaration());
        verify(historyRepository, times(1)).save(any(CustomsStatusHistory.class));
    }
    
    @Test
    void testSubmitDeclaration_FailsCompliance() {
        when(clearanceRepository.findById(clearanceId)).thenReturn(Optional.of(clearance));
        when(complianceEngine.validateClearance(clearance)).thenReturn(Collections.singletonList("Missing Invoice"));
        when(declarationRepository.save(any(CustomsDeclaration.class))).thenAnswer(i -> i.getArgument(0));
        when(clearanceRepository.save(any(CustomsClearance.class))).thenAnswer(i -> i.getArgument(0));

        CustomsDeclaration declaration = new CustomsDeclaration();

        assertThrows(IllegalStateException.class, () -> {
            clearanceService.submitDeclaration(clearanceId, declaration);
        });

        assertEquals(CustomsStatus.DOCUMENTS_PENDING, clearance.getStatus());
    }

    @Test
    void testUpdateStatus_Valid() {
        clearance.setStatus(CustomsStatus.INSPECTION);
        when(clearanceRepository.findById(clearanceId)).thenReturn(Optional.of(clearance));
        when(clearanceRepository.save(any(CustomsClearance.class))).thenAnswer(i -> i.getArgument(0));

        CustomsClearance result = clearanceService.updateStatus(clearanceId, CustomsStatus.CLEARED, "All good");
        
        assertEquals(CustomsStatus.CLEARED, result.getStatus());
        assertEquals("All good", result.getRemarks());
    }
    
    @Test
    void testUpdateStatus_InvalidCleared() {
        clearance.setStatus(CustomsStatus.NOT_STARTED);
        when(clearanceRepository.findById(clearanceId)).thenReturn(Optional.of(clearance));

        assertThrows(IllegalStateException.class, () -> {
            clearanceService.updateStatus(clearanceId, CustomsStatus.CLEARED, "Oops");
        });
    }
}
