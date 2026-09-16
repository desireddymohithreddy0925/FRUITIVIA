package com.fruitivia.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderSequenceService {

    private final OrderSequenceRepository sequenceRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNumber(String prefix) {
        String year = String.valueOf(Year.now().getValue());
        String prefixYear = prefix.toUpperCase() + "-" + year;

        Optional<OrderSequence> optionalSequence = sequenceRepository.findByPrefixYear(prefixYear);

        OrderSequence sequence;
        if (optionalSequence.isPresent()) {
            sequence = optionalSequence.get();
            sequence.setNextValue(sequence.getNextValue() + 1);
        } else {
            sequence = OrderSequence.builder()
                    .prefixYear(prefixYear)
                    .nextValue(1L)
                    .build();
        }

        sequenceRepository.saveAndFlush(sequence);

        return String.format("%s-%04d", prefixYear, sequence.getNextValue());
    }
}
