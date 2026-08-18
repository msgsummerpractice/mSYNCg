package com.example.demo.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface EventServiceInterface<EventRequest, EventResponse, EventViewResponse, EventSpec> {

    EventResponse create(EventRequest request, String username);

    Page<EventViewResponse> getAll(EventSpec spec, Pageable pageable);

}
