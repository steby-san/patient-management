package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.FlywaySchemaHistoryDTO;
import com.patientmanagement.backend.entity.FlywaySchemaHistory;
import com.patientmanagement.backend.repository.FlywaySchemaHistoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class FlywaySchemaHistoryService {

    @Autowired
    private FlywaySchemaHistoryRepository flywaySchemaHistoryRepository;

    public Integer save(FlywaySchemaHistoryVO vO) {
        FlywaySchemaHistory bean = new FlywaySchemaHistory();
        BeanUtils.copyProperties(vO, bean);
        bean = flywaySchemaHistoryRepository.save(bean);
        return bean.getInstalledRank();
    }

    public void delete(Integer id) {
        flywaySchemaHistoryRepository.deleteById(id);
    }

    public void update(Integer id, FlywaySchemaHistoryUpdateVO vO) {
        FlywaySchemaHistory bean = requireOne(id);
        BeanUtils.copyProperties(vO, bean);
        flywaySchemaHistoryRepository.save(bean);
    }

    public FlywaySchemaHistoryDTO getById(Integer id) {
        FlywaySchemaHistory original = requireOne(id);
        return toDTO(original);
    }

    public Page<FlywaySchemaHistoryDTO> query(FlywaySchemaHistoryQueryVO vO) {
        throw new UnsupportedOperationException();
    }

    private FlywaySchemaHistoryDTO toDTO(FlywaySchemaHistory original) {
        FlywaySchemaHistoryDTO bean = new FlywaySchemaHistoryDTO();
        BeanUtils.copyProperties(original, bean);
        return bean;
    }

    private FlywaySchemaHistory requireOne(Integer id) {
        return flywaySchemaHistoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + id));
    }
}
