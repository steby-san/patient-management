package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.MedicationsDTO;
import com.patientmanagement.backend.entity.Medications;
import com.patientmanagement.backend.repository.MedicationsRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class MedicationsService {

    @Autowired
    private MedicationsRepository medicationsRepository;

    public Long save(MedicationsVO vO) {
        Medications bean = new Medications();
        BeanUtils.copyProperties(vO, bean);
        bean = medicationsRepository.save(bean);
        return bean.getId();
    }

    public void delete(Long id) {
        medicationsRepository.deleteById(id);
    }

    public void update(Long id, MedicationsUpdateVO vO) {
        Medications bean = requireOne(id);
        BeanUtils.copyProperties(vO, bean);
        medicationsRepository.save(bean);
    }

    public MedicationsDTO getById(Long id) {
        Medications original = requireOne(id);
        return toDTO(original);
    }

    public Page<MedicationsDTO> query(MedicationsQueryVO vO) {
        throw new UnsupportedOperationException();
    }

    private MedicationsDTO toDTO(Medications original) {
        MedicationsDTO bean = new MedicationsDTO();
        BeanUtils.copyProperties(original, bean);
        return bean;
    }

    private Medications requireOne(Long id) {
        return medicationsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + id));
    }
}
