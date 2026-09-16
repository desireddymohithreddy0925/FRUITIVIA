package com.fruitivia.inventory;

import com.fruitivia.batch.FruitBatch;
import com.fruitivia.common.entity.BaseEntity;
import com.fruitivia.warehouse.StorageLocation;
import com.fruitivia.warehouse.Warehouse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import org.hibernate.envers.Audited;

@Entity
@Table(name = "inventories")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false, unique = true)
    private FruitBatch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @org.hibernate.envers.Audited(targetAuditMode = org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_location_id")
    @org.hibernate.envers.Audited(targetAuditMode = org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED)
    private StorageLocation storageLocation;

    @Column(name = "total_quantity", nullable = false)
    private BigDecimal totalQuantity;

    @Column(name = "available_quantity", nullable = false)
    private BigDecimal availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private BigDecimal reservedQuantity;

    @Column(name = "packed_quantity", nullable = false)
    private BigDecimal packedQuantity;

    @Column(name = "dispatched_quantity", nullable = false)
    private BigDecimal dispatchedQuantity;

    @Column(name = "damaged_quantity", nullable = false)
    private BigDecimal damagedQuantity;

    @Column(name = "expired_quantity", nullable = false)
    private BigDecimal expiredQuantity;
}
