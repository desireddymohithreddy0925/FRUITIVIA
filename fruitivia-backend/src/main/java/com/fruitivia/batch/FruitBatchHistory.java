package com.fruitivia.batch;

import com.fruitivia.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fruit_batch_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FruitBatchHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fruit_batch_id", nullable = false)
    private FruitBatch fruitBatch;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private BatchStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private BatchStatus newStatus;

    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    @Column(columnDefinition = "TEXT")
    private String comments;
}
