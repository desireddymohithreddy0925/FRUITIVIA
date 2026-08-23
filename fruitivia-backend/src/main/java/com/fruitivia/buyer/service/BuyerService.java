package com.fruitivia.buyer.service;

import com.fruitivia.buyer.dto.BuyerDto;
import com.fruitivia.buyer.dto.BuyerMapper;
import com.fruitivia.buyer.repository.BuyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final BuyerMapper buyerMapper;

    @Transactional(readOnly = true)
    public List<BuyerDto> getAllBuyers() {
        return buyerRepository.findAll().stream()
                .map(buyerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BuyerDto getBuyerById(Long id) {
        return buyerRepository.findById(id)
                .map(buyerMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
    }
}
