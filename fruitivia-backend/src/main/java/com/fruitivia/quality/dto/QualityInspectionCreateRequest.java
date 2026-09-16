package com.fruitivia.quality.dto;

import com.fruitivia.quality.QualityGrade;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityInspectionCreateRequest {

    @NotNull(message = "Procurement Item ID is required")
    private UUID procurementItemId;

    @NotNull(message = "Inspected quantity is required")
    @PositiveOrZero(message = "Inspected quantity must be zero or positive")
    private BigDecimal inspectedQuantity;

    @NotNull(message = "Accepted quantity is required")
    @PositiveOrZero(message = "Accepted quantity must be zero or positive")
    private BigDecimal acceptedQuantity;

    @NotNull(message = "Rejected quantity is required")
    @PositiveOrZero(message = "Rejected quantity must be zero or positive")
    private BigDecimal rejectedQuantity;

    private QualityGrade grade;

    private String size;

    private String color;

    private Double weight;

    private String defects;

    private String visualQuality;

    private String phytosanitaryStatus;

    private String notes;
}
