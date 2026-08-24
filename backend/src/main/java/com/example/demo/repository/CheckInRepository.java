package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CheckIn;

import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, Integer> {

    Optional<CheckIn> findByQrCode(String qrCode);
    Optional<CheckIn> findByCode(Long code);
    Optional<CheckIn> findByEventId(Integer eventId);
}
