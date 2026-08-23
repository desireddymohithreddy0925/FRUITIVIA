package com.fruitivia.buyer.repository;

import com.fruitivia.buyer.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    
    @Query("SELECT b FROM Buyer b JOIN FETCH b.user WHERE b.user.id = :userId")
    Optional<Buyer> findByUserId(@Param("userId") Long userId);
}
