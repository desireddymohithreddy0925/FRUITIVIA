package com.fruitivia.document;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentGenerationService documentGenerationService;

    @PostMapping("/generate/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS')")
    public ResponseEntity<ExportDocument> generateDocument(
            @PathVariable UUID orderId,
            @RequestParam DocumentType type) {
        
        ExportDocument document = documentGenerationService.generateDocument(orderId, type);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/{documentId}/url")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENGINEER', 'LOGISTICS', 'BUYER')")
    public ResponseEntity<Map<String, String>> getDownloadUrl(@PathVariable UUID documentId) {
        // In a real app, ensure IDOR checks if user is a buyer
        String url = documentGenerationService.getPresignedUrl(documentId);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
