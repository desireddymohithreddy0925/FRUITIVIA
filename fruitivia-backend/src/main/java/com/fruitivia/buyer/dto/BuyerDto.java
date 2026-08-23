package com.fruitivia.buyer.dto;

import com.fruitivia.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuyerDto {
    private Long id;
    private UserDto user;
    private String companyName;
    private String contactPhone;
    private String taxIdentificationNumber;
    private String defaultCurrency;
}
