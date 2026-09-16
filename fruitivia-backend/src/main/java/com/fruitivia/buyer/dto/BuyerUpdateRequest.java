package com.fruitivia.buyer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyerUpdateRequest {
    @NotBlank
    private String companyName;
    
    private String contactPerson;
    
    @Email
    @NotBlank
    private String email;
    
    private String phone;
    private String country;
    private String address;
    private String destinationCity;
    private String destinationPort;
    private String registrationInformation;
}
