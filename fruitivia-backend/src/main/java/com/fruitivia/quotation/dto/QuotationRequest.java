package com.fruitivia.quotation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class QuotationRequest {
    @NotEmpty
    @Valid
    private List<QuotationItemRequest> items;
}
