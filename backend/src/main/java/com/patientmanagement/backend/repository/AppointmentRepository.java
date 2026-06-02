package com.patientmanagement.backend.repository;

import com.patientmanagement.backend.entity.Appointment;
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
}
