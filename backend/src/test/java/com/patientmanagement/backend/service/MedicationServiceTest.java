package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.MedicationRequestDto;
import com.patientmanagement.backend.dto.MedicationResponseDto;
import com.patientmanagement.backend.exception.EntityNotFoundException;
import com.patientmanagement.backend.repository.MedicationRepository;
import com.patientmanagement.backend.repository.PrescriptionRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("MedicationService Integration Tests")
class MedicationServiceTest {

    @Autowired private MedicationService medicationService;
    @Autowired private MedicationRepository medicationRepository;
    @Autowired private PrescriptionRepository prescriptionRepository;

    @BeforeEach
    void setUp() {
        prescriptionRepository.deleteAll();
        medicationRepository.deleteAll();
    }

    private MedicationRequestDto buildValidDto() {
        return MedicationRequestDto.builder()
                .code("MED001")
                .name("Paracetamol")
                .activeIngredient("Acetaminophen")
                .unit("vien")
                .build();
    }

    @Test
    @DisplayName("1. Thêm thuốc mới thành công")
    void createMedication_ShouldSucceed_WithUniqueCode() {
        MedicationResponseDto result = medicationService.createMedication(buildValidDto());
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("MED001");
        assertThat(medicationRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("2. Fail: Thêm thuốc trùng mã code")
    void createMedication_ShouldThrow_WhenDuplicateCode() {
        medicationService.createMedication(buildValidDto());
        assertThatThrownBy(() -> medicationService.createMedication(buildValidDto()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("3. Lấy danh sách tất cả thuốc")
    void getAllMedications_ShouldReturnList() {
        medicationService.createMedication(buildValidDto());
        MedicationRequestDto dto2 = buildValidDto(); dto2.setCode("MED002"); dto2.setName("Amoxicillin");
        medicationService.createMedication(dto2);

        List<MedicationResponseDto> result = medicationService.getAllMedications();
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("4. Cập nhật thông tin thuốc thành công")
    void updateMedication_ShouldSucceed() {
        MedicationResponseDto created = medicationService.createMedication(buildValidDto());
        MedicationRequestDto updateDto = MedicationRequestDto.builder()
                .code("MED001").name("Paracetamol 500mg").activeIngredient("Acetaminophen 500mg").unit("hop").build();

        MedicationResponseDto result = medicationService.updateMedication(created.getId(), updateDto);
        assertThat(result.getName()).isEqualTo("Paracetamol 500mg");
    }

    @Test
    @DisplayName("5. Fail: Cập nhật sang mã code đã tồn tại")
    void updateMedication_ShouldThrow_WhenNewCodeConflicts() {
        medicationService.createMedication(buildValidDto());
        MedicationRequestDto dto2 = buildValidDto(); dto2.setCode("MED002");
        MedicationResponseDto med2 = medicationService.createMedication(dto2);

        MedicationRequestDto updateDto = buildValidDto(); // Code là MED001
        assertThatThrownBy(() -> medicationService.updateMedication(med2.getId(), updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("6. Xóa thuốc thành công")
    void deleteMedication_ShouldSucceed() {
        MedicationResponseDto created = medicationService.createMedication(buildValidDto());
        medicationService.deleteMedication(created.getId());
        assertThat(medicationRepository.count()).isEqualTo(0);
    }
}