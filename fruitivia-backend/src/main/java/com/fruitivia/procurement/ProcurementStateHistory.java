package com.fruitivia.procurement;

import com.fruitivia.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "procurement_state_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcurementStateHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procurement_order_id", nullable = false)
    private ProcurementOrder procurementOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private ProcurementStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private ProcurementStatus newStatus;

    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    @Column(columnDefinition = "TEXT")
    private String comments;
}
