package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.PrescriptionCreateDto;
import com.patientmanagement.backend.dto.PrescriptionItemDto;
import com.patientmanagement.backend.dto.PrescriptionResponseDto;
import com.patientmanagement.backend.entity.Medication;
import com.patientmanagement.backend.entity.Patient;
import com.patientmanagement.backend.exception.EntityNotFoundException;
import com.patientmanagement.backend.repository.MedicationRepository;
import com.patientmanagement.backend.repository.PatientRepository;
import com.patientmanagement.backend.repository.PrescriptionRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PrescriptionService Integration Tests")
class PrescriptionServiceTest {

    @Autowired private PrescriptionService prescriptionService;
    @Autowired private PrescriptionRepository prescriptionRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private MedicationRepository medicationRepository;

    private Patient testPatient;
    private Medication testMedication;

    @BeforeEach
    void setUp() {
        prescriptionRepository.deleteAll();
        medicationRepository.deleteAll();
        patientRepository.deleteAll();

        testPatient = patientRepository.save(Patient.builder().patientCode("BN-2026-0001").fullName("Nguyen Van A").build());
        testMedication = medicationRepository.save(Medication.builder().code("MED001").name("Paracetamol").unit("vien").build());
    }

    private PrescriptionCreateDto buildValidDto() {
        return PrescriptionCreateDto.builder()
                .patientId(testPatient.getId())
                .doctorName("Dr. Binh")
                .startDate(LocalDate.of(2026, 6, 1))
                .endDate(LocalDate.of(2026, 6, 7))
                .diagnosis("Cam cum")
                .items(List.of(PrescriptionItemDto.builder()
                        .medicationId(testMedication.getId())
                        .dosage("Uong 1 vien sau an")
                        .quantity(10)
                        .build()))
                .build();
    }

    @Test
    @DisplayName("1. Kê đơn thành công với dữ liệu hợp lệ")
    void createPrescription_ShouldSucceed_WithValidData() {
        PrescriptionResponseDto result = prescriptionService.createPrescription(buildValidDto());
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("2. Fail: Start date lớn hơn End date")
    void createPrescription_ShouldThrow_WhenStartAfterEnd() {
        PrescriptionCreateDto dto = buildValidDto();
        dto.setStartDate(LocalDate.of(2026, 6, 10));
        dto.setEndDate(LocalDate.of(2026, 6, 1));

        assertThatThrownBy(() -> prescriptionService.createPrescription(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start date must be before or equal to end date");
    }

    @Test
    @DisplayName("3. Fail: Danh sách thuốc rỗng")
    void createPrescription_ShouldThrow_WhenItemsEmpty() {
        PrescriptionCreateDto dto = buildValidDto();
        dto.setItems(Collections.emptyList());

        assertThatThrownBy(() -> prescriptionService.createPrescription(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one medication item");
    }

    @Test
    @DisplayName("4. Fail: Số lượng thuốc <= 0")
    void createPrescription_ShouldThrow_WhenQuantityInvalid() {
        PrescriptionCreateDto dto = buildValidDto();
        dto.getItems().get(0).setQuantity(0);

        assertThatThrownBy(() -> prescriptionService.createPrescription(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be greater than 0");
    }

    @Test
    @DisplayName("5. Fail: Liều dùng (dosage) để trống")
    void createPrescription_ShouldThrow_WhenDosageBlank() {
        PrescriptionCreateDto dto = buildValidDto();
        dto.getItems().get(0).setDosage("   ");

        assertThatThrownBy(() -> prescriptionService.createPrescription(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dosage is required");
    }

    @Test
    @DisplayName("6. Kiểm tra Cascade: Lưu đơn thuốc tự động lưu các Item vào DB")
    void createPrescription_ShouldCascadeSaveItems() {
        prescriptionService.createPrescription(buildValidDto());

        assertThat(prescriptionRepository.count()).isEqualTo(1);
        assertThat(prescriptionRepository.findAll().get(0).getItems()).hasSize(1);
    }
}