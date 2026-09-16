package com.fruitivia.customs;

import com.fruitivia.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customs_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomsStatusHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clearance_id", nullable = false)
    private CustomsClearance clearance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomsStatus status;

    @Column(length = 1000)
    private String notes;

    @Column
    private String actor;
}
