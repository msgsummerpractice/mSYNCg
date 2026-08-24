package com.example.demo.repository;

import com.example.demo.model.Registration;
import com.example.demo.model.RegistrationStatus;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Integer> {

    Boolean existsByUserIdAndEventId(Integer userId, Integer eventId);

    Optional<Registration> findByUserIdAndEventIdAndStatus(
            Integer userId,
            Integer eventId,
            RegistrationStatus status
    );

    Boolean existsByUserIdAndEventIdAndStatus(
            Integer userId,
            Integer eventId,
            RegistrationStatus status
    );
}