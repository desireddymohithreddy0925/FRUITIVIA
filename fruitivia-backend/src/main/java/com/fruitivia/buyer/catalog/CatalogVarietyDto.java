package com.fruitivia.buyer.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogVarietyDto {
    private UUID id;
    private String name;
    private Integer shelfLifeDays;
    private Double minStorageTemperature;
    private Double maxStorageTemperature;
}
