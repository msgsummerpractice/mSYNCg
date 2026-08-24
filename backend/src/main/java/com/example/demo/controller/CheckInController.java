package com.example.demo.controller;

import com.example.demo.dto.request.CheckInRequest;
import com.example.demo.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/check-in")
@RequiredArgsConstructor
public class CheckInController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<Void> checkIn(
            @Valid @RequestBody CheckInRequest request
    ) {
        attendanceService.checkIn(request.value());

        return ResponseEntity.ok().build();
    }
}