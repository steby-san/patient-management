package com.patientmanagement.backend.repository;

import com.patientmanagement.backend.entity.PrescriptionItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PrescriptionItemsRepository extends JpaRepository<PrescriptionItems, Long>, JpaSpecificationExecutor<PrescriptionItems> {

}