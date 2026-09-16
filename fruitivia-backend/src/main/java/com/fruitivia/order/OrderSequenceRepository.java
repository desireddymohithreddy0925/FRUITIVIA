package com.fruitivia.order;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderSequenceRepository extends JpaRepository<OrderSequence, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OrderSequence> findByPrefixYear(String prefixYear);
}
