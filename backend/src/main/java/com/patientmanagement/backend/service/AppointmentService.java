package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.AppointmentCreateDto;
import com.patientmanagement.backend.dto.AppointmentResponseDto;
import com.patientmanagement.backend.entity.Appointment;
import com.patientmanagement.backend.entity.Patient;
import com.patientmanagement.backend.exception.EntityNotFoundException;
import com.patientmanagement.backend.repository.AppointmentRepository;
import com.patientmanagement.backend.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

        if (appointmentRepository.existsByPatientIdAndAppointmentTime(dto.getPatientId(), dto.getAppointmentTime())) {
            throw new IllegalArgumentException("This patient already has an appointment at the selected time");
        }

        // 2. Validate: Bác sĩ không được trùng lịch
        if (appointmentRepository.existsByDoctorNameAndAppointmentTime(dto.getDoctorName(), dto.getAppointmentTime())) {
            throw new IllegalArgumentException("This doctor already has an appointment at the selected time");
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

    @Transactional
    public AppointmentResponseDto updateStatus(Long id, String status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with id: " + id));

        String newStatus = status.toUpperCase();
        String currentStatus = appointment.getStatus();

        // 3. Validate: Kiểm tra luồng chuyển đổi trạng thái (State Machine)
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        appointment.setStatus(newStatus);
        return toResponseDto(appointmentRepository.save(appointment));
    }

    // Helper method kiểm tra luồng trạng thái: PENDING -> CONFIRMED -> COMPLETED/CANCELLED
    private boolean isValidStatusTransition(String current, String next) {
        if ("PENDING".equals(current)) {
            return "CONFIRMED".equals(next) || "CANCELLED".equals(next);
        }
        if ("CONFIRMED".equals(current)) {
            return "COMPLETED".equals(next) || "CANCELLED".equals(next);
        }

        return false;
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return appointmentRepository.findByDateRange(start, end)
                .stream()
                .map(this::toResponseDto)
                .toList();
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

    @Transactional
    public AppointmentResponseDto updateAppointment(Long id, AppointmentCreateDto dto) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with id: " + id));


        if ("COMPLETED".equals(appointment.getStatus()) || "CANCELLED".equals(appointment.getStatus())) {
            throw new IllegalStateException("Cannot update an appointment that is already " + appointment.getStatus());
        }


        if (appointmentRepository.existsByPatientIdAndAppointmentTimeAndIdNot(dto.getPatientId(), dto.getAppointmentTime(), id)) {
            throw new IllegalArgumentException("This patient already has an appointment at the selected time");
        }

        // 4. Validate trùng lịch cho Bác sĩ (Loại trừ chính ID đang sửa)
        if (appointmentRepository.existsByDoctorNameAndAppointmentTimeAndIdNot(dto.getDoctorName(), dto.getAppointmentTime(), id)) {
            throw new IllegalArgumentException("This doctor already has an appointment at the selected time");
        }


        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + dto.getPatientId()));


        appointment.setPatient(patient);
        appointment.setAppointmentTime(dto.getAppointmentTime());
        appointment.setDoctorName(dto.getDoctorName());
        appointment.setReason(dto.getReason());



        return toResponseDto(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found with id: " + id));
        return toResponseDto(appointment);
    }
}