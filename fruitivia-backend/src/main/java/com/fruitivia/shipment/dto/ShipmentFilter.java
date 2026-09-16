package com.fruitivia.shipment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentFilter {
    private String country;
    private String port;
    private String status;
    private Instant expectedDeliveryFrom;
    private Instant expectedDeliveryTo;
    private String customsStatus;
}
