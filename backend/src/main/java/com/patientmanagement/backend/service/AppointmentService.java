package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.AppointmentCreateDto;
import com.patientmanagement.backend.dto.AppointmentResponseDto;
import com.patientmanagement.backend.entity.Appointment;
import com.patientmanagement.backend.exception.EntityNotFoundException;
import com.patientmanagement.backend.repository.AppointmentRepository;
import com.patientmanagement.backend.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentCreateDto dto) {
        var patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + dto.getPatientId()));

        // Validate overlapping (Requirement: 3.3)
        if (appointmentRepository.countOverlappingAppointments(dto.getAppointmentTime()) > 0) {
            throw new IllegalArgumentException("Appointment time is already booked");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .appointmentTime(dto.getAppointmentTime())
                .status("PENDING")
                .reason(dto.getReason())
                .doctorName(dto.getDoctorName())
                .build();

        return toResponseDto(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponseDto> getAppointmentsByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return appointmentRepository.findByDateRange(start, end)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponseDto updateStatus(Long id, String status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with id: " + id));
        
        appointment.setStatus(status.toUpperCase());
        return toResponseDto(appointmentRepository.save(appointment));
    }

    private AppointmentResponseDto toResponseDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getFullName())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .reason(appointment.getReason())
                .doctorName(appointment.getDoctorName())
                .build();
    }
}
