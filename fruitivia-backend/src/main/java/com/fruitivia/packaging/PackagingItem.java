package com.fruitivia.packaging;

import com.fruitivia.batch.FruitBatch;
import com.fruitivia.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "packaging_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackagingItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packaging_record_id", nullable = false)
    private PackagingRecord packagingRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fruit_batch_id", nullable = false)
    private FruitBatch fruitBatch;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "packaging_type", nullable = false)
    private String packagingType;

    @Column(name = "package_count", nullable = false)
    private Integer packageCount;

    @Column(name = "package_weight")
    private BigDecimal packageWeight;

    @Column
    private String dimensions;
}
