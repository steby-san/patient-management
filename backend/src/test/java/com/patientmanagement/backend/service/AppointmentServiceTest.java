package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.AppointmentCreateDto;
import com.patientmanagement.backend.dto.AppointmentResponseDto;
import com.patientmanagement.backend.entity.Patient;
import com.patientmanagement.backend.exception.EntityNotFoundException;
import com.patientmanagement.backend.repository.AppointmentRepository;
import com.patientmanagement.backend.repository.PatientRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Tự động rollback dữ liệu sau mỗi test method
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("AppointmentService Integration Tests")
class AppointmentServiceTest {

    @Autowired private AppointmentService appointmentService;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private PatientRepository patientRepository;

    private Patient testPatient;
    private final LocalDateTime testTime = LocalDateTime.of(2026, 6, 5, 10, 0);

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();

        testPatient = patientRepository.save(Patient.builder()
                .patientCode("BN-2026-0001")
                .fullName("Nguyen Van A")
                .build());
    }

    @Test
    @DisplayName("1. Tạo lịch hẹn thành công khi không trùng lịch")
    void createAppointment_ShouldSucceed_WhenNoOverlapping() {
        AppointmentCreateDto dto = AppointmentCreateDto.builder()
                .patientId(testPatient.getId())
                .doctorName("Dr. Binh")
                .appointmentTime(testTime)
                .reason("Kham tong quat")
                .build();

        AppointmentResponseDto result = appointmentService.createAppointment(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(appointmentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("2. Fail: Bệnh nhân đặt trùng giờ")
    void createAppointment_ShouldThrow_WhenPatientOverlapping() {
        AppointmentCreateDto dto1 = AppointmentCreateDto.builder()
                .patientId(testPatient.getId()).doctorName("Dr. Binh").appointmentTime(testTime).reason("Lan 1").build();
        appointmentService.createAppointment(dto1);

        AppointmentCreateDto dto2 = AppointmentCreateDto.builder()
                .patientId(testPatient.getId()).doctorName("Dr. Khac").appointmentTime(testTime).reason("Lan 2").build();

        assertThatThrownBy(() -> appointmentService.createAppointment(dto2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("patient already has an appointment");
    }

    @Test
    @DisplayName("3. Fail: Bác sĩ bị trùng giờ với bệnh nhân khác")
    void createAppointment_ShouldThrow_WhenDoctorOverlapping() {
        Patient patient2 = patientRepository.save(Patient.builder().patientCode("BN-2026-0002").fullName("Tran B").build());

        appointmentService.createAppointment(AppointmentCreateDto.builder()
                .patientId(testPatient.getId()).doctorName("Dr. Binh").appointmentTime(testTime).reason("Kham A").build());

        AppointmentCreateDto dto2 = AppointmentCreateDto.builder()
                .patientId(patient2.getId()).doctorName("Dr. Binh").appointmentTime(testTime).reason("Kham B").build();

        assertThatThrownBy(() -> appointmentService.createAppointment(dto2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("doctor already has an appointment");
    }

    @Test
    @DisplayName("4. State Machine: Chuyển PENDING -> CONFIRMED thành công")
    void updateStatus_ShouldTransitionFromPendingToConfirmed() {
        AppointmentResponseDto created = appointmentService.createAppointment(AppointmentCreateDto.builder()
                .patientId(testPatient.getId()).doctorName("Dr. Binh").appointmentTime(testTime).reason("Test").build());

        AppointmentResponseDto updated = appointmentService.updateStatus(created.getId(), "CONFIRMED");
        assertThat(updated.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("5. State Machine: Fail khi nhảy cóc từ PENDING -> COMPLETED")
    void updateStatus_ShouldThrow_WhenSkipConfirmed() {
        AppointmentResponseDto created = appointmentService.createAppointment(AppointmentCreateDto.builder()
                .patientId(testPatient.getId()).doctorName("Dr. Binh").appointmentTime(testTime).reason("Test").build());

        assertThatThrownBy(() -> appointmentService.updateStatus(created.getId(), "COMPLETED"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition");
    }
}