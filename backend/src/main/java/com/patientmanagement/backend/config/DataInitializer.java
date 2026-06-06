package com.patientmanagement.backend.config;

import com.patientmanagement.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
    * In order to set the admin password when the application starts
 */
@Slf4j
@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        userRepository.findByUsername("admin").ifPresentOrElse(admin -> {

            String newHash = passwordEncoder.encode("password123");

            admin.setPassword(newHash);
            userRepository.save(admin);
        }, () -> {
        });
    }
}
