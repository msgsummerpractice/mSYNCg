package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import com.example.demo.model.EventStatus;
import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.MissingLocationException;
import com.example.demo.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.example.demo.filtering.events.EventSpec;
import com.example.demo.model.Event;
import com.example.demo.model.EventType;
import com.example.demo.model.Location;

import org.modelmapper.ModelMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.User;

@RequiredArgsConstructor
@Service
public class EventService implements EventServiceInterface<EventRequest, EventResponse, EventViewResponse, EventSpec> {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public EventResponse create(EventRequest eventRequest, String username) {

        Event event = modelMapper.map(eventRequest, Event.class);
        User user = userRepository.findByEmail(username);
        event.setStatus(EventStatus.DRAFT);
        event.setCreatedBy(user);
        checkConditions(eventRequest, event);
        eventRepository.save(event);
        return modelMapper.map(event, EventResponse.class);
    }

    public void checkConditions(EventRequest eventRequest, Event event) {
        if (eventRequest.getType() != null) {
            if (eventRequest.getLocation() == null && eventRequest.getType() != EventType.INTERNAL) {
                throw new MissingLocationException("Event location is required for non-internal events.");
            }
            else if(eventRequest.getType() == EventType.INTERNAL) {
                event.setLocation(Location.ALL);
            }
        }
    }

    @Override
    public Page<EventViewResponse> getAll(EventSpec spec, Pageable pageable) {
        Page<Event> eventsPage = eventRepository.findAll(spec, pageable);

        return eventsPage.map(event -> modelMapper.map(event, EventViewResponse.class));
    }

}
