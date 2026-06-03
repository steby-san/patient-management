package com.patientmanagement.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@jakarta.persistence.Table(name = "flyway_schema_history")
@lombok.NoArgsConstructor
@lombok.experimental.SuperBuilder
@lombok.ToString
@lombok.Setter
@lombok.Getter
@jakarta.persistence.Entity
@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@Table(name = "flyway_schema_history")
public class FlywaySchemaHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @jakarta.persistence.Column(name = "installed_rank", nullable = false)
    @jakarta.persistence.Id
    @Id
    @Column(name = "installed_rank", nullable = false)
    private Integer installedRank;

    @jakarta.persistence.Column(name = "version")
    @Column(name = "version")
    private String version;

    @jakarta.persistence.Column(name = "description", nullable = false)
    @Column(name = "description", nullable = false)
    private String description;

    @jakarta.persistence.Column(name = "type", nullable = false)
    @Column(name = "type", nullable = false)
    private String type;

    @jakarta.persistence.Column(name = "script", nullable = false)
    @Column(name = "script", nullable = false)
    private String script;

    @jakarta.persistence.Column(name = "checksum")
    @Column(name = "checksum")
    private Integer checksum;

    @jakarta.persistence.Column(name = "installed_by", nullable = false)
    @Column(name = "installed_by", nullable = false)
    private String installedBy;

    @jakarta.persistence.Column(name = "installed_on", nullable = false)
    @Column(name = "installed_on", nullable = false)
    private LocalDateTime installedOn = LocalDateTime.now();

    @jakarta.persistence.Column(name = "execution_time", nullable = false)
    @Column(name = "execution_time", nullable = false)
    private Integer executionTime;

    @jakarta.persistence.Column(name = "success", nullable = false)
    @Column(name = "success", nullable = false)
    private Integer success;

}
