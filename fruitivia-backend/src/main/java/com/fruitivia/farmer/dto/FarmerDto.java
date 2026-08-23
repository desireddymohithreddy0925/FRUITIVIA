package com.fruitivia.farmer.dto;

import com.fruitivia.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FarmerDto {
    private Long id;
    private UserDto user;
    private String phoneNumber;
    private String taxId;
    private String bankAccountNumber;
    private String bankName;
}
