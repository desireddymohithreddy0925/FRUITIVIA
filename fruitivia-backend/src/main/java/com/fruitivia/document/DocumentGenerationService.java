package com.fruitivia.document;

import com.fruitivia.order.Order;
import com.fruitivia.order.OrderRepository;
import com.fruitivia.storage.FileStorageService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import com.fruitivia.buyer.Buyer;
import com.fruitivia.buyer.BuyerRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentGenerationService {

    private final SpringTemplateEngine templateEngine;
    private final FileStorageService fileStorageService;
    private final ExportDocumentRepository exportDocumentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;

    @Transactional
    public ExportDocument generateDocument(UUID orderId, DocumentType type) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("buyer", order.getBuyer());
        context.setVariable("quotation", order.getQuotation());
        context.setVariable("generatedDate", OffsetDateTime.now());

        String templateName = getTemplateNameForType(type);
        String htmlContent = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();

            byte[] pdfBytes = os.toByteArray();
            
            String filename = generateFilename(order, type);
            String storageKey = "docs/" + order.getId() + "/" + filename;

            try (ByteArrayInputStream is = new ByteArrayInputStream(pdfBytes)) {
                fileStorageService.upload(storageKey, is, "application/pdf", pdfBytes.length);
            }

            String username = SecurityContextHolder.getContext().getAuthentication() != null 
                    ? SecurityContextHolder.getContext().getAuthentication().getName() 
                    : "system";

            ExportDocument doc = ExportDocument.builder()
                    .order(order)
                    .type(type)
                    .storageKey(storageKey)
                    .filename(filename)
                    .contentType("application/pdf")
                    .sizeBytes((long) pdfBytes.length)
                    .generatedBy(username)
                    .generatedAt(OffsetDateTime.now())
                    .version(1)
                    .build();

            doc.setActive(true);
            return exportDocumentRepository.save(doc);

        } catch (Exception e) {
            log.error("Failed to generate document", e);
            throw new RuntimeException("Failed to generate document", e);
        }
    }

    public String getPresignedUrl(UUID documentId) {
        ExportDocument doc = exportDocumentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
                
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(email).orElse(null);
            
            if (currentUser != null && currentUser.getRole() == com.fruitivia.user.Role.BUYER) {
                Buyer buyer = buyerRepository.findByUserId(currentUser.getId()).orElse(null);
                if (buyer == null || !buyer.getId().equals(doc.getOrder().getBuyer().getId())) {
                    throw new org.springframework.security.access.AccessDeniedException("Access denied");
                }
            }
        }
                
        return fileStorageService.getPresignedUrl(doc.getStorageKey(), 3600); // 1 hour expiry
    }

    private String getTemplateNameForType(DocumentType type) {
        switch (type) {
            case PROFORMA_INVOICE:
                return "documents/proforma-invoice";
            case COMMERCIAL_INVOICE:
                return "documents/commercial-invoice";
            case PACKING_LIST:
                return "documents/packing-list";
            // Other templates can be mapped here as they are created
            default:
                return "documents/default";
        }
    }

    private String generateFilename(Order order, DocumentType type) {
        return order.getOrderNumber() + "_" + type.name() + ".pdf";
    }
}
