package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.PrescriptionItemsDTO;
import com.patientmanagement.backend.entity.PrescriptionItems;
import com.patientmanagement.backend.repository.PrescriptionItemsRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class PrescriptionItemsService {

    @Autowired
    private PrescriptionItemsRepository prescriptionItemsRepository;

    public Long save(PrescriptionItemsVO vO) {
        PrescriptionItems bean = new PrescriptionItems();
        BeanUtils.copyProperties(vO, bean);
        bean = prescriptionItemsRepository.save(bean);
        return bean.getId();
    }

    public void delete(Long id) {
        prescriptionItemsRepository.deleteById(id);
    }

    public void update(Long id, PrescriptionItemsUpdateVO vO) {
        PrescriptionItems bean = requireOne(id);
        BeanUtils.copyProperties(vO, bean);
        prescriptionItemsRepository.save(bean);
    }

    public PrescriptionItemsDTO getById(Long id) {
        PrescriptionItems original = requireOne(id);
        return toDTO(original);
    }

    public Page<PrescriptionItemsDTO> query(PrescriptionItemsQueryVO vO) {
        throw new UnsupportedOperationException();
    }

    private PrescriptionItemsDTO toDTO(PrescriptionItems original) {
        PrescriptionItemsDTO bean = new PrescriptionItemsDTO();
        BeanUtils.copyProperties(original, bean);
        return bean;
    }

    private PrescriptionItems requireOne(Long id) {
        return prescriptionItemsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + id));
    }
}
