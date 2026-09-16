package com.fruitivia.quality.dto;

import com.fruitivia.quality.InspectionStatus;
import com.fruitivia.quality.QualityGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityInspectionDto {
    private UUID id;
    private String inspectionNumber;
    private UUID procurementItemId;
    private UUID inspectorId;
    private String inspectorName;
    private BigDecimal inspectedQuantity;
    private BigDecimal acceptedQuantity;
    private BigDecimal rejectedQuantity;
    private QualityGrade grade;
    private String size;
    private String color;
    private Double weight;
    private String defects;
    private String visualQuality;
    private String phytosanitaryStatus;
    private Instant inspectionDate;
    private String notes;
    private InspectionStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
