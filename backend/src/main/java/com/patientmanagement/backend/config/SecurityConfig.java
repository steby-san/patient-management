package com.patientmanagement.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientmanagement.backend.security.JwtAuthenticationFilter;
import com.patientmanagement.backend.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtTokenProvider, objectMapper());

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**", "/actuator/health").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/docs.html").permitAll()
                .requestMatchers("/api/v1/prescriptions/**").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers("/api/reports/**").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers("/api/v1/appointments/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/api/v1/patients/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/api/v1/health-metrics/**", "/api/medications/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
