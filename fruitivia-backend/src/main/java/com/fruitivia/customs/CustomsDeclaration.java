package com.fruitivia.customs;

import com.fruitivia.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "customs_declarations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomsDeclaration extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(name = "export_country", nullable = false, length = 2)
    private String exportCountry;

    @Column(name = "destination_country", nullable = false, length = 2)
    private String destinationCountry;

    @Column(name = "export_port")
    private String exportPort;

    @Column(name = "destination_port")
    private String destinationPort;

    @Column(name = "customs_broker")
    private String customsBroker;

    @Column(name = "duties_and_taxes")
    private BigDecimal dutiesAndTaxes;

    @Column(name = "declaration_date")
    private OffsetDateTime declarationDate;
}
