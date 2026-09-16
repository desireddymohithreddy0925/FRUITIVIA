package com.fruitivia.packaging;

import com.fruitivia.common.entity.BaseEntity;
import com.fruitivia.order.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "packaging_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackagingRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackagingStatus status;

    @Column(name = "packing_date")
    private OffsetDateTime packingDate;

    @Column(name = "packed_by")
    private String packedBy;

    @Column
    private String notes;

    @Builder.Default
    @OneToMany(mappedBy = "packagingRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackagingItem> items = new ArrayList<>();

    public void addItem(PackagingItem item) {
        items.add(item);
        item.setPackagingRecord(this);
    }
}
