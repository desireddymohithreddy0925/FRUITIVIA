package com.fruitivia.quality;

import com.fruitivia.common.entity.BaseEntity;
import com.fruitivia.procurement.ProcurementItem;
import com.fruitivia.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.envers.Audited;

@Entity
@Table(name = "quality_inspections")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QualityInspection extends BaseEntity {

    @Column(name = "inspection_number", unique = true, nullable = false)
    private String inspectionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procurement_item_id", nullable = false)
    @org.hibernate.envers.Audited(targetAuditMode = org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED)
    private ProcurementItem procurementItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id", nullable = false)
    @org.hibernate.envers.Audited(targetAuditMode = org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED)
    private User inspector;

    @Column(name = "inspected_quantity", nullable = false)
    private BigDecimal inspectedQuantity;

    @Column(name = "accepted_quantity", nullable = false)
    private BigDecimal acceptedQuantity;

    @Column(name = "rejected_quantity", nullable = false)
    private BigDecimal rejectedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade")
    private QualityGrade grade;

    @Column(name = "size")
    private String size;

    @Column(name = "color")
    private String color;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "defects", columnDefinition = "TEXT")
    private String defects;

    @Column(name = "visual_quality")
    private String visualQuality;

    @Column(name = "phytosanitary_status")
    private String phytosanitaryStatus;

    @Column(name = "inspection_date", nullable = false)
    private Instant inspectionDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InspectionStatus status;
}
