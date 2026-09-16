package com.fruitivia.buyer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerDto {
    private UUID id;
    private String companyName;
    private String contactPerson;
    private String email; // Maps from contactEmail
    private String phone; // Maps from contactNumber
    private String country;
    private String address;
    private String destinationCity;
    private String destinationPort;
    private String registrationInformation;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
