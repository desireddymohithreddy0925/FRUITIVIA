package com.fruitivia.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FruitBatchSequenceService {

    private final BatchSequenceRepository sequenceRepository;

    /**
     * Generates a unique batch number in a separate transaction to ensure it is not locked
     * or rolled back by the main business transaction unnecessarily, or if it is, the sequence still advances.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateBatchNumber(String prefix) {
        String year = String.valueOf(Year.now().getValue());
        String prefixYear = prefix.toUpperCase() + "-" + year;

        Optional<BatchSequence> optionalSequence = sequenceRepository.findByPrefixYear(prefixYear);

        BatchSequence sequence;
        if (optionalSequence.isPresent()) {
            sequence = optionalSequence.get();
            sequence.setNextValue(sequence.getNextValue() + 1);
        } else {
            sequence = BatchSequence.builder()
                    .prefixYear(prefixYear)
                    .nextValue(1L)
                    .build();
        }

        sequenceRepository.saveAndFlush(sequence);

        return String.format("%s-%04d", prefixYear, sequence.getNextValue());
    }
}
