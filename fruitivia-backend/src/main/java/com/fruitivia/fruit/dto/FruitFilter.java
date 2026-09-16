package com.fruitivia.fruit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FruitFilter {
    private String name;
    private List<String> varieties;
    private String origin;
    private String category;
    private String season;
}
