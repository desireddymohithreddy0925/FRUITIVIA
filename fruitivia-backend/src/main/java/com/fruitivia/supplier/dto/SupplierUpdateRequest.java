package com.fruitivia.supplier.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierUpdateRequest {

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
