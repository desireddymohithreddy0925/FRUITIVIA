package com.fruitivia.fruit;

import com.fruitivia.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fruit_varieties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FruitVariety extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fruit_id", nullable = false)
    private Fruit fruit;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    @Column(name = "min_storage_temperature")
    private Double minStorageTemperature;

    @Column(name = "max_storage_temperature")
    private Double maxStorageTemperature;
}
