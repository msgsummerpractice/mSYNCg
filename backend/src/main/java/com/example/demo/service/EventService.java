package com.example.demo.service;

import com.example.demo.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import com.example.demo.model.EventStatus;
import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.MissingLocationException;
import com.example.demo.repository.EventRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.example.demo.filtering.events.EventSpec;
import com.example.demo.model.Event;
import com.example.demo.model.EventType;

import org.modelmapper.ModelMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.User;

@RequiredArgsConstructor
@Service
public class EventService implements EventServiceInterface {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public void validateEvent(EventRequest eventRequest, Event event) {
        if (eventRequest.getLocation() == null && eventRequest.getType() != EventType.INTERNAL) {
            throw new MissingLocationException("Event location is required for non-internal events.");
        }
    }

    @Override
    public EventResponse create(EventRequest eventRequest, String username) {

        Event event = modelMapper.map(eventRequest, Event.class);
        User user;
        try {
            user = userRepository.findByEmail(username);
        } catch (Exception e) {
            throw new NotFoundException("User not found with email: " + username);
        }

        event.setStatus(EventStatus.DRAFT);
        event.setCreatedBy(user);
        validateEvent(eventRequest, event);
        eventRepository.save(event);
        return modelMapper.map(event, EventResponse.class);
    }

    @Override
    public Page<EventViewResponse> getAll(EventSpec spec, Pageable pageable) {
        Page<Event> eventsPage = eventRepository.findAll(spec, pageable);

        return eventsPage.map(event -> modelMapper.map(event, EventViewResponse.class));
    }

}
