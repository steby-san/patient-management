package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.PrescriptionsDTO;
import com.patientmanagement.backend.entity.Prescriptions;
import com.patientmanagement.backend.repository.PrescriptionsRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class PrescriptionsService {

    @Autowired
    private PrescriptionsRepository prescriptionsRepository;

    public Long save(PrescriptionsVO vO) {
        Prescriptions bean = new Prescriptions();
        BeanUtils.copyProperties(vO, bean);
        bean = prescriptionsRepository.save(bean);
        return bean.getId();
    }

    public void delete(Long id) {
        prescriptionsRepository.deleteById(id);
    }

    public void update(Long id, PrescriptionsUpdateVO vO) {
        Prescriptions bean = requireOne(id);
        BeanUtils.copyProperties(vO, bean);
        prescriptionsRepository.save(bean);
    }

    public PrescriptionsDTO getById(Long id) {
        Prescriptions original = requireOne(id);
        return toDTO(original);
    }

    public Page<PrescriptionsDTO> query(PrescriptionsQueryVO vO) {
        throw new UnsupportedOperationException();
    }

    private PrescriptionsDTO toDTO(Prescriptions original) {
        PrescriptionsDTO bean = new PrescriptionsDTO();
        BeanUtils.copyProperties(original, bean);
        return bean;
    }

    private Prescriptions requireOne(Long id) {
        return prescriptionsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + id));
    }
}
