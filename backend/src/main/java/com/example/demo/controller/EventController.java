package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;

import com.example.demo.filtering.events.EventSpec;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@EnableMethodSecurity
public class EventController {

    private final EventService eventService;

    @PreAuthorize("hasRole('MARKETING_ORGANIZER')")
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest eventRequest,
            Authentication authentication) {
        EventResponse eventResponse = eventService.create(eventRequest, authentication.getName());

        return ResponseEntity.ok(eventResponse);
    }

    @GetMapping
    public ResponseEntity<Page<EventViewResponse>> getEvents(
            EventSpec eventSpec,
            Pageable pageable) {
        Page<EventViewResponse> response = eventService.getAll(eventSpec, pageable);
        return ResponseEntity.ok(response);
    }

}
