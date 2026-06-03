package com.patientmanagement.backend.repository;

import com.patientmanagement.backend.entity.Appointment;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentTime >= :start AND a.appointmentTime <= :end")
    List<Appointment> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentTime = :time AND a.status != 'CANCELLED'")
    long countOverlappingAppointments(@Param("time") LocalDateTime time);

    boolean existsByPatientIdAndAppointmentTime(Long patientId, LocalDateTime appointmentTime);
    boolean existsByDoctorNameAndAppointmentTime(String doctorName, LocalDateTime appointmentTime);

    boolean existsByPatientIdAndAppointmentTimeAndIdNot(@NotNull Long patientId, @NotNull @Future LocalDateTime appointmentTime, Long id);

    boolean existsByDoctorNameAndAppointmentTimeAndIdNot(String doctorName, @NotNull @Future LocalDateTime appointmentTime, Long id);
}
