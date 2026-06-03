package com.patientmanagement.backend.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class MedicationsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private String code;

    private String name;

    private String activeIngredient;

    private String unit;

}
