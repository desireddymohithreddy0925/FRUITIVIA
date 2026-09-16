package com.fruitivia.batch;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "batch_sequences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchSequence {
    @Id
    @Column(name = "prefix_year", nullable = false, unique = true)
    private String prefixYear; // e.g. "MAN-2026"

    @Column(name = "next_value", nullable = false)
    private Long nextValue;
}
