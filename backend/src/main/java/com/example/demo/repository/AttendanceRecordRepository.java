package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.AttendanceRecord;

public interface AttendanceRecordRepository
        extends JpaRepository<AttendanceRecord, Integer> {

    boolean existsByUserIdAndCheckInId(
            Integer userId,
            Integer checkInId
    );
    
    @Query("SELECT CASE WHEN COUNT(ar) > 0 THEN true ELSE false END " +
           "FROM AttendanceRecord ar " +
           "WHERE ar.user.id = :userId AND ar.checkIn.event.id = :eventId")
    boolean existsByUserIdAndEventId(
            @Param("userId") Integer userId,
            @Param("eventId") Integer eventId
    );
}