package com.fruitivia.buyer;

import com.fruitivia.buyer.dto.BuyerDto;
import com.fruitivia.buyer.dto.BuyerUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/buyers")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerService buyerService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<BuyerDto> getMyProfile() {
        return ResponseEntity.ok(buyerService.getMyProfile());
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<BuyerDto> updateMyProfile(@Valid @RequestBody BuyerUpdateRequest request) {
        return ResponseEntity.ok(buyerService.updateMyProfile(request));
    }
}
