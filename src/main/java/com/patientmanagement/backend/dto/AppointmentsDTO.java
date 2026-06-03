package com.patientmanagement.backend.dto;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AppointmentsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private Long patientId;

    private LocalDateTime appointmentTime;


    /**
     * PENDING, CONFIRMED, COMPLETED, CANCELLED
     */
    private String status;

    private String reason;

    private String doctorName;

}
