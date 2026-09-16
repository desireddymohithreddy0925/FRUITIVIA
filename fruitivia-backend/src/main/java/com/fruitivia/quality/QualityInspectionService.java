package com.fruitivia.quality;

import com.fruitivia.procurement.ProcurementItem;
import com.fruitivia.procurement.ProcurementItemRepository;
import com.fruitivia.quality.dto.QualityInspectionCreateRequest;
import com.fruitivia.quality.dto.QualityInspectionDto;
import com.fruitivia.quality.dto.QualityInspectionStatusUpdateRequest;
import com.fruitivia.user.User;
import com.fruitivia.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QualityInspectionService {

    private final QualityInspectionRepository inspectionRepository;
    private final QualityInspectionHistoryRepository historyRepository;
    private final ProcurementItemRepository procurementItemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<QualityInspectionDto> getAllInspections(Pageable pageable) {
        return inspectionRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public QualityInspectionDto getInspectionById(UUID id) {
        return inspectionRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Inspection not found"));
    }

    @Transactional
    public QualityInspectionDto createInspection(QualityInspectionCreateRequest request, String currentUserEmail) {
        ProcurementItem item = procurementItemRepository.findById(request.getProcurementItemId())
                .orElseThrow(() -> new EntityNotFoundException("Procurement item not found"));

        User inspector = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Inspector user not found"));

        // Validate quantities
        if (request.getAcceptedQuantity().add(request.getRejectedQuantity()).compareTo(request.getInspectedQuantity()) != 0) {
            throw new IllegalArgumentException("Accepted + Rejected quantities must equal Inspected quantity");
        }
        
        if (request.getInspectedQuantity().compareTo(item.getQuantity()) > 0) {
            throw new IllegalArgumentException("Cannot inspect more than the procured quantity");
        }

        String inspectionNumber = "QC-" + System.currentTimeMillis();

        QualityInspection inspection = QualityInspection.builder()
                .inspectionNumber(inspectionNumber)
                .procurementItem(item)
                .inspector(inspector)
                .inspectedQuantity(request.getInspectedQuantity())
                .acceptedQuantity(request.getAcceptedQuantity())
                .rejectedQuantity(request.getRejectedQuantity())
                .grade(request.getGrade())
                .size(request.getSize())
                .color(request.getColor())
                .weight(request.getWeight())
                .defects(request.getDefects())
                .visualQuality(request.getVisualQuality())
                .phytosanitaryStatus(request.getPhytosanitaryStatus())
                .notes(request.getNotes())
                .inspectionDate(Instant.now())
                .status(InspectionStatus.PENDING)
                .build();
        inspection.setActive(true);

        QualityInspection savedInspection = inspectionRepository.save(inspection);
        saveHistory(savedInspection, null, InspectionStatus.PENDING, currentUserEmail, "Inspection created");

        return mapToDto(savedInspection);
    }

    @Transactional
    public QualityInspectionDto updateStatus(UUID id, QualityInspectionStatusUpdateRequest request, String currentUserEmail) {
        QualityInspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inspection not found"));

        InspectionStatus oldStatus = inspection.getStatus();
        InspectionStatus newStatus = request.getStatus();

        if (oldStatus == newStatus) {
            return mapToDto(inspection);
        }

        if (!isValidTransition(oldStatus, newStatus)) {
            throw new IllegalArgumentException("Invalid state transition from " + oldStatus + " to " + newStatus);
        }

        inspection.setStatus(newStatus);
        QualityInspection savedInspection = inspectionRepository.save(inspection);

        saveHistory(savedInspection, oldStatus, newStatus, currentUserEmail, request.getComments());

        return mapToDto(savedInspection);
    }

    private boolean isValidTransition(InspectionStatus current, InspectionStatus next) {
        return switch (current) {
            case PENDING -> next != InspectionStatus.PENDING;
            case REQUIRES_REINSPECTION -> next == InspectionStatus.PASSED || next == InspectionStatus.FAILED || next == InspectionStatus.PARTIALLY_PASSED;
            case PASSED, FAILED, PARTIALLY_PASSED -> false; // Terminal states
        };
    }

    private void saveHistory(QualityInspection inspection, InspectionStatus prev, InspectionStatus current, String userEmail, String comments) {
        QualityInspectionHistory history = QualityInspectionHistory.builder()
                .qualityInspection(inspection)
                .previousStatus(prev)
                .newStatus(current)
                .changedBy(userEmail)
                .comments(comments)
                .build();
        history.setActive(true);
        historyRepository.save(history);
    }

    private QualityInspectionDto mapToDto(QualityInspection inspection) {
        return QualityInspectionDto.builder()
                .id(inspection.getId())
                .inspectionNumber(inspection.getInspectionNumber())
                .procurementItemId(inspection.getProcurementItem().getId())
                .inspectorId(inspection.getInspector().getId())
                .inspectorName(inspection.getInspector().getName())
                .inspectedQuantity(inspection.getInspectedQuantity())
                .acceptedQuantity(inspection.getAcceptedQuantity())
                .rejectedQuantity(inspection.getRejectedQuantity())
                .grade(inspection.getGrade())
                .size(inspection.getSize())
                .color(inspection.getColor())
                .weight(inspection.getWeight())
                .defects(inspection.getDefects())
                .visualQuality(inspection.getVisualQuality())
                .phytosanitaryStatus(inspection.getPhytosanitaryStatus())
                .inspectionDate(inspection.getInspectionDate())
                .notes(inspection.getNotes())
                .status(inspection.getStatus())
                .createdAt(inspection.getCreatedAt())
                .updatedAt(inspection.getUpdatedAt())
                .build();
    }
}
