package com.patientmanagement.backend.dto;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FlywaySchemaHistoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer installedRank;

    private String version;

    private String description;

    private String type;

    private String script;

    private Integer checksum;

    private String installedBy;

    private LocalDateTime installedOn;

    private Integer executionTime;

    private Integer success;

}
