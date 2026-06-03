package com.patientmanagement.backend.service;

import com.patientmanagement.backend.dto.AppointmentsDTO;
import com.patientmanagement.backend.entity.Appointments;
import com.patientmanagement.backend.repository.AppointmentsRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class AppointmentsService {

    @Autowired
    private AppointmentsRepository appointmentsRepository;

    public Long save(AppointmentsVO vO) {
        Appointments bean = new Appointments();
        BeanUtils.copyProperties(vO, bean);
        bean = appointmentsRepository.save(bean);
        return bean.getId();
    }

    public void delete(Long id) {
        appointmentsRepository.deleteById(id);
    }

    public void update(Long id, AppointmentsUpdateVO vO) {
        Appointments bean = requireOne(id);
        BeanUtils.copyProperties(vO, bean);
        appointmentsRepository.save(bean);
    }

    public AppointmentsDTO getById(Long id) {
        Appointments original = requireOne(id);
        return toDTO(original);
    }

    public Page<AppointmentsDTO> query(AppointmentsQueryVO vO) {
        throw new UnsupportedOperationException();
    }

    private AppointmentsDTO toDTO(Appointments original) {
        AppointmentsDTO bean = new AppointmentsDTO();
        BeanUtils.copyProperties(original, bean);
        return bean;
    }

    private Appointments requireOne(Long id) {
        return appointmentsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resource not found: " + id));
    }
}
