package com.fruitivia.buyer;

import com.fruitivia.buyer.dto.BuyerDto;
import com.fruitivia.buyer.dto.BuyerUpdateRequest;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public BuyerDto getMyProfile() {
        UUID userId = getCurrentUserId();
        Buyer buyer = buyerRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Buyer profile not found for current user"));
        return mapToDto(buyer);
    }

    @Transactional
    public BuyerDto updateMyProfile(BuyerUpdateRequest request) {
        UUID userId = getCurrentUserId();
        Buyer buyer = buyerRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Buyer profile not found for current user"));

        buyer.setCompanyName(request.getCompanyName());
        buyer.setContactPerson(request.getContactPerson());
        buyer.setContactEmail(request.getEmail());
        buyer.setContactNumber(request.getPhone());
        buyer.setCountry(request.getCountry());
        buyer.setAddress(request.getAddress());
        buyer.setDestinationCity(request.getDestinationCity());
        buyer.setDestinationPort(request.getDestinationPort());
        buyer.setRegistrationInformation(request.getRegistrationInformation());

        buyer = buyerRepository.save(buyer);
        return mapToDto(buyer);
    }

    private UUID getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getId();
    }

    private BuyerDto mapToDto(Buyer buyer) {
        return BuyerDto.builder()
                .id(buyer.getId())
                .companyName(buyer.getCompanyName())
                .contactPerson(buyer.getContactPerson())
                .email(buyer.getContactEmail())
                .phone(buyer.getContactNumber())
                .country(buyer.getCountry())
                .address(buyer.getAddress())
                .destinationCity(buyer.getDestinationCity())
                .destinationPort(buyer.getDestinationPort())
                .registrationInformation(buyer.getRegistrationInformation())
                .active(buyer.isActive())
                .createdAt(buyer.getCreatedAt())
                .updatedAt(buyer.getUpdatedAt())
                .build();
    }
}
