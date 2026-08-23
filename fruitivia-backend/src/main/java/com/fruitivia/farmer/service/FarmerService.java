package com.fruitivia.farmer.service;

import com.fruitivia.farmer.dto.FarmerDto;
import com.fruitivia.farmer.dto.FarmerMapper;
import com.fruitivia.farmer.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FarmerService {

    private final FarmerRepository farmerRepository;
    private final FarmerMapper farmerMapper;

    @Transactional(readOnly = true)
    public List<FarmerDto> getAllFarmers() {
        return farmerRepository.findAll().stream()
                .map(farmerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FarmerDto getFarmerById(Long id) {
        return farmerRepository.findById(id)
                .map(farmerMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
    }
}
