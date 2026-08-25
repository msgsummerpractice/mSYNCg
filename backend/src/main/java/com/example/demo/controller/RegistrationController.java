package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

import com.example.demo.service.RegistrationService;
import com.example.demo.dto.request.RegistrationLookupRequest;
import com.example.demo.dto.request.RegistrationRequest;
import com.example.demo.dto.response.RegistrationDetailsResponse;
import com.example.demo.dto.response.RegistrationResponse;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<RegistrationResponse> createRegistration(@Valid @RequestBody RegistrationRequest request) {
        RegistrationResponse response = registrationService.createRegistration(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<RegistrationDetailsResponse> getRegistration(
            @RequestParam Integer eventId,
            @RequestParam Integer userId) {
        return ResponseEntity.ok(registrationService.getRegistration(eventId, userId));
    }

    @PutMapping
    public ResponseEntity<RegistrationDetailsResponse> updateRegistration(
            @Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(registrationService.updateRegistration(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteRegistration(@Valid @RequestBody RegistrationLookupRequest request) {
        registrationService.deleteRegistration(request.getEventId(), request.getUserId());
        return ResponseEntity.noContent().build();
    }

}
