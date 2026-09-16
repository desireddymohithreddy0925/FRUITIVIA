package com.fruitivia.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCreateRequest {

    private String supplierCode; // Optional. If missing, system generates one.

    @NotBlank(message = "Name is required")
    private String name;

    private String contactPerson;

    private String phone;

    @Email(message = "Invalid email format")
    private String contactEmail;

    private String address;

    private String region;

    private String farmLocation;

    private String farmingInformation;
}
