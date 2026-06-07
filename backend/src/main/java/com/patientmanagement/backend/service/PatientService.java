package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.PageResponse;
import com.patientmanagement.backend.dto.PatientCreateDto;
import com.patientmanagement.backend.dto.PatientResponseDto;
import com.patientmanagement.backend.entity.Patient;
import com.patientmanagement.backend.exception.DuplicatePatientCodeException;
import com.patientmanagement.backend.exception.EntityNotFoundException;
import com.patientmanagement.backend.repository.PatientRepository;
import com.patientmanagement.backend.specification.PatientSpecification;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientSpecification patientSpecification;

    public PatientService(PatientRepository patientRepository,
                          PatientSpecification patientSpecification) {
        this.patientRepository = patientRepository;
        this.patientSpecification = patientSpecification;
    }

    // ----------------------------------------------------------------
    // Task 6.2 — generatePatientCode
    // ----------------------------------------------------------------
    private String generatePatientCode() {
        int year = LocalDate.now().getYear();
        long count = patientRepository.countAllByPatientCodePattern("BN-" + year + "-%");
        if (count + 1 > 9999) {
            throw new IllegalStateException("Patient code sequence exhausted for year " + year);
        }
        return "BN-" + year + "-" + String.format("%04d", count + 1);
    }

    // ----------------------------------------------------------------
    // Task 6.4 — createPatient, getPatient, getAllPatients
    // ----------------------------------------------------------------
    public PatientResponseDto createPatient(PatientCreateDto dto) {
        String patientCode = generatePatientCode();

        Patient patient = Patient.builder()
                .patientCode(patientCode)
                .fullName(dto.getFullName())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .build();

        Patient saved;
        try {
            saved = patientRepository.save(patient);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatePatientCodeException("Patient code conflict, please retry");
        }

        return toResponseDto(saved);
    }

    public PatientResponseDto getPatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
        return toResponseDto(patient);
    }

    public PageResponse<PatientResponseDto> getAllPatients(int page, int size, String search) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Patient> result = patientRepository.findAll(
                PatientSpecification.withSearch(search), pageable);

        List<PatientResponseDto> data = result.getContent().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        return PageResponse.<PatientResponseDto>builder()
                .data(data)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .page(result.getNumber())
                .size(result.getSize())
                .build();
    }

    // ----------------------------------------------------------------
    // Task 6.5 — updatePatient, deletePatient
    // ----------------------------------------------------------------
    public PatientResponseDto updatePatient(Long id, PatientCreateDto dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));

        // Copy only the mutable fields; patientCode and createdAt are never touched
        patient.setFullName(dto.getFullName());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setEmail(dto.getEmail());
        patient.setAddress(dto.getAddress());

        Patient saved = patientRepository.save(patient);
        return toResponseDto(saved);
    }

    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
        // Hibernate executes the @SQLDelete soft-delete SQL
        patientRepository.delete(patient);
    }

    // ----------------------------------------------------------------
    // Mapping helper
    // ----------------------------------------------------------------
    private PatientResponseDto toResponseDto(Patient patient) {
        return PatientResponseDto.builder()
                .id(patient.getId())
                .patientCode(patient.getPatientCode())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .phoneNumber(patient.getPhoneNumber())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .createdAt(patient.getCreatedAt())
                .build();
    }
}
