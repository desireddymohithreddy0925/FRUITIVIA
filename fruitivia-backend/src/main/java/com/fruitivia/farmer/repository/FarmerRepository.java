package com.fruitivia.farmer.repository;

import com.fruitivia.farmer.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    
    @Query("SELECT f FROM Farmer f JOIN FETCH f.user WHERE f.user.id = :userId")
    Optional<Farmer> findByUserId(@Param("userId") Long userId);
}
