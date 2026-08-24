package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.AttendanceRecord;

public interface AttendanceRecordRepository
        extends JpaRepository<AttendanceRecord, Integer> {

    boolean existsByUserIdAndCheckInId(
            Integer userId,
            Integer checkInId
    );
}