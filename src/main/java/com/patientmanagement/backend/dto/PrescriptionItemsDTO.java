package com.patientmanagement.backend.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class PrescriptionItemsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private Long prescriptionId;

    private Long medicationId;


    /**
     * VD: Uống 1 viên sau ăn
     */
    private String dosage;

    private Integer quantity;

}
