package com.patientmanagement.backend.specification;

import com.patientmanagement.backend.entity.Patient;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PatientSpecification {

    public static Specification<Patient> withSearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("fullName")), pattern),
                cb.like(cb.lower(root.get("patientCode")), pattern),
                cb.like(cb.lower(root.get("phoneNumber")), pattern)
            );
        };
    }
}
