package com.fruitivia.document;

import com.fruitivia.buyer.Buyer;
import com.fruitivia.order.Order;
import com.fruitivia.order.OrderRepository;
import com.fruitivia.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentGenerationServiceTest {

    @Mock
    private SpringTemplateEngine templateEngine;
    
    @Mock
    private FileStorageService fileStorageService;
    
    @Mock
    private ExportDocumentRepository exportDocumentRepository;
    
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DocumentGenerationService documentGenerationService;

    private Order order;
    private Buyer buyer;
    private UUID orderId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        buyer = new Buyer();
        buyer.setCompanyName("Test Buyer Co");

        order = new Order();
        order.setId(orderId);
        order.setOrderNumber("ORD-12345");
        order.setBuyer(buyer);
    }

    @Test
    void testGenerateDocument_Success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        // Mock Thymeleaf processing
        when(templateEngine.process(eq("documents/proforma-invoice"), any(IContext.class)))
                .thenReturn("<html><body><h1>Test</h1></body></html>");
                
        // Mock save
        when(exportDocumentRepository.save(any(ExportDocument.class))).thenAnswer(i -> {
            ExportDocument doc = i.getArgument(0);
            doc.setId(UUID.randomUUID());
            return doc;
        });

        ExportDocument doc = documentGenerationService.generateDocument(orderId, DocumentType.PROFORMA_INVOICE);

        assertNotNull(doc.getId());
        assertEquals(DocumentType.PROFORMA_INVOICE, doc.getType());
        assertEquals("ORD-12345_PROFORMA_INVOICE.pdf", doc.getFilename());
        assertEquals("application/pdf", doc.getContentType());
        
        // Verify storage was called
        verify(fileStorageService, times(1)).upload(
                contains("docs/" + orderId + "/ORD-12345_PROFORMA_INVOICE.pdf"),
                any(InputStream.class),
                eq("application/pdf"),
                anyLong()
        );
    }
}
