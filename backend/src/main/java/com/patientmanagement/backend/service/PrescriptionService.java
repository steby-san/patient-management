package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.PrescriptionCreateDto;
import com.patientmanagement.backend.dto.PrescriptionItemDto;
import com.patientmanagement.backend.dto.PrescriptionResponseDto;
import com.patientmanagement.backend.entity.Medication;
import com.patientmanagement.backend.entity.Prescription;
import com.patientmanagement.backend.entity.PrescriptionItem;
import com.patientmanagement.backend.exception.EntityNotFoundException;
import com.patientmanagement.backend.repository.MedicationRepository;
import com.patientmanagement.backend.repository.PatientRepository;
import com.patientmanagement.backend.repository.PrescriptionRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final MedicationRepository medicationRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PatientRepository patientRepository,
                               MedicationRepository medicationRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.medicationRepository = medicationRepository;
    }

    @Transactional
    public PrescriptionResponseDto createPrescription(PrescriptionCreateDto dto) {

        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        var patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + dto.getPatientId()));

        Prescription prescription = Prescription.builder()
                .patient(patient)
                .doctorName(dto.getDoctorName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .diagnosis(dto.getDiagnosis())
                .build();


        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Prescription must contain at least one medication item");
        }

        List<PrescriptionItem> items = dto.getItems().stream().map(itemDto -> {

            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0 for medication ID: " + itemDto.getMedicationId());
            }

            if (itemDto.getDosage() == null || itemDto.getDosage().trim().isEmpty()) {
                throw new IllegalArgumentException("Dosage is required for medication ID: " + itemDto.getMedicationId());
            }

            var medication = medicationRepository.findById(itemDto.getMedicationId())
                    .orElseThrow(() -> new EntityNotFoundException("Medication not found with id: " + itemDto.getMedicationId()));

            return PrescriptionItem.builder()
                    .medication(medication)
                    .dosage(itemDto.getDosage())
                    .quantity(itemDto.getQuantity())
                    .build();
        }).toList(); 


        items.forEach(prescription::addItem);

        return toResponseDto(prescriptionRepository.save(prescription));
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getByPatient(Long patientId) {
        return prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrescriptionResponseDto getDetail(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found with id: " + id));
        return toResponseDto(prescription);
    }

    private PrescriptionResponseDto toResponseDto(Prescription prescription) {
        List<PrescriptionItemDto> items = prescription.getItems().stream()
                .map(item -> PrescriptionItemDto.builder()
                        .medicationId(item.getMedication().getId())
                        .medicationName(item.getMedication().getName())
                        .dosage(item.getDosage())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        return PrescriptionResponseDto.builder()
                .id(prescription.getId())
                .patientId(prescription.getPatient().getId())
                .patientName(prescription.getPatient().getFullName())
                .doctorName(prescription.getDoctorName())
                .startDate(prescription.getStartDate())
                .endDate(prescription.getEndDate())
                .diagnosis(prescription.getDiagnosis())
                .createdAt(prescription.getCreatedAt())
                .items(items)
                .build();
    }


    @Transactional
    public PrescriptionResponseDto updatePrescription(Long id, PrescriptionCreateDto dto) {

        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found with id: " + id));


        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }


        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Prescription must contain at least one medication item");
        }

        // 4. Build danh sách item mới
        List<PrescriptionItem> newItems = dto.getItems().stream().map(itemDto -> {
            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
            if (itemDto.getDosage() == null || itemDto.getDosage().trim().isEmpty()) {
                throw new IllegalArgumentException("Dosage is required");
            }

            Medication medication = medicationRepository.findById(itemDto.getMedicationId())
                    .orElseThrow(() -> new EntityNotFoundException("Medication not found"));

            return PrescriptionItem.builder()
                    .medication(medication)
                    .dosage(itemDto.getDosage())
                    .quantity(itemDto.getQuantity())
                    .build();
        }).toList();


        prescription.setStartDate(dto.getStartDate());
        prescription.setEndDate(dto.getEndDate());
        prescription.setDiagnosis(dto.getDiagnosis());

        // 6. Xử lý items: Xóa cũ -> Thêm mới (kích hoạt orphanRemoval)
        prescription.getItems().clear();
        for (PrescriptionItem item : newItems) {
            prescription.addItem(item); // Dùng helper method để set bidirectional
        }

        return toResponseDto(prescriptionRepository.save(prescription));
    }

    @Transactional
    public void deletePrescription(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found with id: " + id));


        prescriptionRepository.delete(prescription);
    }

}