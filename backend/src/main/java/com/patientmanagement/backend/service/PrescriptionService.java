package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.PrescriptionCreateDto;
import com.patientmanagement.backend.dto.PrescriptionItemDto;
import com.patientmanagement.backend.dto.PrescriptionResponseDto;
import com.patientmanagement.backend.entity.Prescription;
import com.patientmanagement.backend.entity.PrescriptionItem;
import com.patientmanagement.backend.exception.EntityNotFoundException;
import com.patientmanagement.backend.repository.MedicationRepository;
import com.patientmanagement.backend.repository.PatientRepository;
import com.patientmanagement.backend.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        var patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + dto.getPatientId()));

        Prescription prescription = Prescription.builder()
                .patient(patient)
                .doctorName(dto.getDoctorName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .diagnosis(dto.getDiagnosis())
                .build();

        List<PrescriptionItem> items = dto.getItems().stream().map(itemDto -> {
            var medication = medicationRepository.findById(itemDto.getMedicationId())
                    .orElseThrow(() -> new EntityNotFoundException("Medication not found with id: " + itemDto.getMedicationId()));
            return PrescriptionItem.builder()
                    .prescription(prescription)
                    .medication(medication)
                    .dosage(itemDto.getDosage())
                    .quantity(itemDto.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        prescription.setItems(items);
        return toResponseDto(prescriptionRepository.save(prescription));
    }

    public List<PrescriptionResponseDto> getByPatient(Long patientId) {
        return prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

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
                .collect(Collectors.toList());

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
}
