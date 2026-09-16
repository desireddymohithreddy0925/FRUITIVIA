package com.fruitivia.supplier;

import com.fruitivia.supplier.dto.SupplierCreateRequest;
import com.fruitivia.supplier.dto.SupplierDto;
import com.fruitivia.supplier.dto.SupplierUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public Page<SupplierDto> searchSuppliers(Map<String, String> criteria, Pageable pageable) {
        return supplierRepository.findAll(SupplierSpecification.searchByCriteria(criteria), pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public SupplierDto getSupplierById(UUID id) {
        return supplierRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with ID: " + id));
    }

    @Transactional
    public SupplierDto createSupplier(SupplierCreateRequest request) {
        String code = StringUtils.hasText(request.getSupplierCode()) 
                ? request.getSupplierCode() 
                : "SUP-" + System.currentTimeMillis();

        if (supplierRepository.findBySupplierCode(code).isPresent()) {
            throw new IllegalArgumentException("Supplier code already exists: " + code);
        }

        Supplier supplier = Supplier.builder()
                .supplierCode(code)
                .name(request.getName())
                .contactPerson(request.getContactPerson())
                .phone(request.getPhone())
                .contactEmail(request.getContactEmail())
                .address(request.getAddress())
                .region(request.getRegion())
                .farmLocation(request.getFarmLocation())
                .farmingInformation(request.getFarmingInformation())
                .build();
        
        supplier.setActive(true);

        return mapToDto(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierDto updateSupplier(UUID id, SupplierUpdateRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with ID: " + id));

        if (StringUtils.hasText(request.getName())) supplier.setName(request.getName());
        if (StringUtils.hasText(request.getContactPerson())) supplier.setContactPerson(request.getContactPerson());
        if (StringUtils.hasText(request.getPhone())) supplier.setPhone(request.getPhone());
        if (StringUtils.hasText(request.getContactEmail())) supplier.setContactEmail(request.getContactEmail());
        if (StringUtils.hasText(request.getAddress())) supplier.setAddress(request.getAddress());
        if (StringUtils.hasText(request.getRegion())) supplier.setRegion(request.getRegion());
        if (StringUtils.hasText(request.getFarmLocation())) supplier.setFarmLocation(request.getFarmLocation());
        if (StringUtils.hasText(request.getFarmingInformation())) supplier.setFarmingInformation(request.getFarmingInformation());

        return mapToDto(supplierRepository.save(supplier));
    }

    @Transactional
    public void deactivateSupplier(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with ID: " + id));
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }

    @Transactional
    public void activateSupplier(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with ID: " + id));
        supplier.setActive(true);
        supplierRepository.save(supplier);
    }

    private SupplierDto mapToDto(Supplier supplier) {
        return SupplierDto.builder()
                .id(supplier.getId())
                .supplierCode(supplier.getSupplierCode())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .phone(supplier.getPhone())
                .contactEmail(supplier.getContactEmail())
                .address(supplier.getAddress())
                .region(supplier.getRegion())
                .farmLocation(supplier.getFarmLocation())
                .farmingInformation(supplier.getFarmingInformation())
                .active(supplier.isActive())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}
