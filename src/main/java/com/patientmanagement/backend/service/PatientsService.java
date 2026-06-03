package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.PatientsDTO;
import com.patientmanagement.backend.entity.Patients;
import com.patientmanagement.backend.repository.PatientsRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class PatientsService {

    @Autowired
    private PatientsRepository patientsRepository;

    public Long save(PatientsVO vO) {
        Patients bean = new Patients();
        BeanUtils.copyProperties(vO, bean);
        bean = patientsRepository.save(bean);
        return bean.getId();
    }

    public void delete(Long id) {
        patientsRepository.deleteById(id);
    }

    public void update(Long id, PatientsUpdateVO vO) {
        Patients bean = requireOne(id);
        BeanUtils.copyProperties(vO, bean);
        patientsRepository.save(bean);
    }

    public PatientsDTO getById(Long id) {
        Patients original = requireOne(id);
        return toDTO(original);
    }

    public Page<PatientsDTO> query(PatientsQueryVO vO) {
        throw new UnsupportedOperationException();
    }

    private PatientsDTO toDTO(Patients original) {
        PatientsDTO bean = new PatientsDTO();
        BeanUtils.copyProperties(original, bean);
        return bean;
    }

    private Patients requireOne(Long id) {
        return patientsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + id));
    }
}
