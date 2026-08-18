package com.example.demo.controller;

import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import com.example.demo.filtering.events.EventSpec;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Page<EventViewResponse>> getEvents(
        EventSpec eventSpec,
        Pageable pageable) {
            Page<EventViewResponse> response = eventService.getAll(eventSpec, pageable);
            return ResponseEntity.ok(response);
        }

    @GetMapping("/{id}")
    public ResponseEntity<EventDetailsResponse> getEventDetails(@PathVariable Integer id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

}
