package com.fruitivia.order;

import com.fruitivia.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "order_sequences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSequence extends BaseEntity {

    @Column(name = "prefix_year", nullable = false, unique = true)
    private String prefixYear;

    @Column(name = "next_value", nullable = false)
    private Long nextValue;
}
