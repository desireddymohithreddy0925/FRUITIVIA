package com.fruitivia.buyer.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogFruitDto {
    private UUID id;
    private String name;
    private String description;
    private List<CatalogVarietyDto> varieties;
}
