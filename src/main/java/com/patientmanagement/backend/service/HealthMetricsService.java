package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.HealthMetricsDTO;
import com.patientmanagement.backend.entity.HealthMetrics;
import com.patientmanagement.backend.repository.HealthMetricsRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class HealthMetricsService {

    @Autowired
    private HealthMetricsRepository healthMetricsRepository;

    public Long save(HealthMetricsVO vO) {
        HealthMetrics bean = new HealthMetrics();
        BeanUtils.copyProperties(vO, bean);
        bean = healthMetricsRepository.save(bean);
        return bean.getId();
    }

    public void delete(Long id) {
        healthMetricsRepository.deleteById(id);
    }

    public void update(Long id, HealthMetricsUpdateVO vO) {
        HealthMetrics bean = requireOne(id);
        BeanUtils.copyProperties(vO, bean);
        healthMetricsRepository.save(bean);
    }

    public HealthMetricsDTO getById(Long id) {
        HealthMetrics original = requireOne(id);
        return toDTO(original);
    }

    public Page<HealthMetricsDTO> query(HealthMetricsQueryVO vO) {
        throw new UnsupportedOperationException();
    }

    private HealthMetricsDTO toDTO(HealthMetrics original) {
        HealthMetricsDTO bean = new HealthMetricsDTO();
        BeanUtils.copyProperties(original, bean);
        return bean;
    }

    private HealthMetrics requireOne(Long id) {
        return healthMetricsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + id));
    }
}
