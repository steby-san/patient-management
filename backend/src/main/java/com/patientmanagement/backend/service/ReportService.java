package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.TopBmiPatientDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final EntityManager entityManager;

    public ReportService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Requirement 4.1: Top N patients with highest BMI using Window Function
     */
    @SuppressWarnings("unchecked")
    public List<TopBmiPatientDto> getTopBmiPatients(int limit) {
        String sql = """
            SELECT p.patient_code, p.full_name, latest_metrics.weight, latest_metrics.height,
                   (latest_metrics.weight / POWER(latest_metrics.height / 100, 2)) as bmi,
                   latest_metrics.measured_at
            FROM patients p
            JOIN (
                SELECT patient_id, weight, height, measured_at,
                       ROW_NUMBER() OVER (PARTITION BY patient_id ORDER BY measured_at DESC) as rn
                FROM health_metrics
                WHERE weight > 0 AND height > 0
            ) latest_metrics ON p.id = latest_metrics.patient_id
            WHERE latest_metrics.rn = 1
            ORDER BY bmi DESC
            LIMIT :limit
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("limit", limit);

        List<Object[]> results = query.getResultList();
        return results.stream().map(row -> TopBmiPatientDto.builder()
                .patientCode((String) row[0])
                .fullName((String) row[1])
                .weight(((Number) row[2]).doubleValue())
                .height(((Number) row[3]).doubleValue())
                .bmi(((Number) row[4]).doubleValue())
                .measuredAt(((java.sql.Timestamp) row[5]).toLocalDateTime())
                .build()
        ).collect(Collectors.toList());
    }

    /**
     * Requirement 3.5: High Blood Pressure Ratio in last 14 days
     */
    public Map<String, Object> getHighBpRatio() {
        String sql = """
            SELECT 
                COUNT(*) as total_measured,
                SUM(CASE WHEN systolic >= 140 OR diastolic >= 90 THEN 1 ELSE 0 END) as high_bp_count
            FROM health_metrics
            WHERE measured_at >= :since
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("since", LocalDate.now().minusDays(14).atStartOfDay());

        Object[] result = (Object[]) query.getSingleResult();
        long total = ((Number) result[0]).longValue();
        long high = ((Number) result[1]).longValue();
        double ratio = total > 0 ? (double) high / total : 0;

        Map<String, Object> response = new HashMap<>();
        response.put("totalMeasured", total);
        response.put("highBpCount", high);
        response.put("ratio", ratio);
        return response;
    }

    /**
     * Requirement 3.5: Active medications today
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getActiveMedications() {
        String sql = """
            SELECT DISTINCT m.code, m.name, m.active_ingredient
            FROM medications m
            JOIN prescription_items pi ON m.id = pi.medication_id
            JOIN prescriptions p ON pi.prescription_id = p.id
            WHERE :today BETWEEN p.start_date AND p.end_date
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("today", LocalDate.now());

        List<Object[]> results = query.getResultList();
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("code", row[0]);
            map.put("name", row[1]);
            map.put("activeIngredient", row[2]);
            return map;
        }).collect(Collectors.toList());
    }
}
