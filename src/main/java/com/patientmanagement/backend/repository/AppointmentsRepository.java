package com.patientmanagement.backend.repository;

import com.patientmanagement.backend.entity.Appointments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppointmentsRepository extends JpaRepository<Appointments, Long>, JpaSpecificationExecutor<Appointments> {

}