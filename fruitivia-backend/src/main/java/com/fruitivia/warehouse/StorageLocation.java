package com.fruitivia.warehouse;

import com.fruitivia.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "storage_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageLocation extends BaseEntity {

    @Column(name = "location_code", nullable = false)
    private String locationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private BigDecimal capacity; // in metric tons

    @Column(name = "current_usage", nullable = false)
    private BigDecimal currentUsage;

}
