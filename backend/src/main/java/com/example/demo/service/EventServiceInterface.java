package com.example.demo.service;

import org.springframework.data.domain.Pageable;

import com.example.demo.dto.response.EventResponse;
import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.filtering.events.EventSpec;
import com.example.demo.model.Location;

import org.springframework.data.domain.Page;

import java.util.List;

public interface EventServiceInterface {

    EventResponse create(EventRequest request, String username);

    Page<EventViewResponse> getAll(EventSpec spec, List<Location> locations, Pageable pageable, Integer userId);

}
