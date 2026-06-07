package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.MedicationRequestDto;
import com.patientmanagement.backend.dto.MedicationResponseDto;
import com.patientmanagement.backend.entity.Medication;
import com.patientmanagement.backend.exception.EntityNotFoundException; // Giả sử bạn đã tạo class này
import com.patientmanagement.backend.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;

    @Transactional
    public MedicationResponseDto createMedication(MedicationRequestDto dto) {
        // 1. Validate mã thuốc trùng lặp
        if (medicationRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Medication code '" + dto.getCode() + "' already exists");
        }

        // 2. Map DTO to Entity và lưu
        Medication medication = Medication.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .activeIngredient(dto.getActiveIngredient())
                .unit(dto.getUnit())
                .build();

        try {
            Medication savedMedication = medicationRepository.save(medication);
            return mapToResponseDto(savedMedication);
        } catch (DataIntegrityViolationException e) {
            // Fallback nếu có race condition (2 request cùng tạo 1 mã thuốc 1 lúc)
            throw new IllegalArgumentException("Medication code already exists");
        }
    }

    @Transactional(readOnly = true)
    public List<MedicationResponseDto> getAllMedications() {
        return medicationRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedicationResponseDto getMedicationById(Long id) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medication not found with id: " + id));
        return mapToResponseDto(medication);
    }


    @Transactional
    public MedicationResponseDto updateMedication(Long id, MedicationRequestDto dto) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medication not found with id: " + id));

        // Kiểm tra nếu mã code mới bị trùng với mã của thuốc khác
        if (!medication.getCode().equals(dto.getCode()) && medicationRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Medication code '" + dto.getCode() + "' already exists");
        }

        // Cập nhật các trường
        medication.setCode(dto.getCode());
        medication.setName(dto.getName());
        medication.setActiveIngredient(dto.getActiveIngredient());
        medication.setUnit(dto.getUnit());

        return mapToResponseDto(medicationRepository.save(medication));
    }


    @Transactional
    public void deleteMedication(Long id) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medication not found with id: " + id));

        try {
            medicationRepository.delete(medication);
        } catch (DataIntegrityViolationException e) {
            // Nếu thuốc đang được sử dụng trong prescription_items, DB sẽ báo lỗi Foreign Key
            throw new IllegalStateException("Cannot delete medication because it is currently prescribed to patients");
        }
    }



    private MedicationResponseDto mapToResponseDto(Medication medication) {
        return MedicationResponseDto.builder()
                .id(medication.getId())
                .code(medication.getCode())
                .name(medication.getName())
                .activeIngredient(medication.getActiveIngredient())
                .unit(medication.getUnit())
                .build();
    }
}