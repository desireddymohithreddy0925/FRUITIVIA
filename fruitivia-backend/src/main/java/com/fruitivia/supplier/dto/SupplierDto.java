package com.fruitivia.supplier.dto;

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
public class SupplierDto {

    private UUID id;
    private String supplierCode;
    private String name;
    private String contactPerson;
    private String phone;
    private String contactEmail;
    private String address;
    private String region;
    private String farmLocation;
    private String farmingInformation;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
