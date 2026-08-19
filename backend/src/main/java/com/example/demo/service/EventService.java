package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Base64;

import org.modelmapper.ModelMapper;

import java.util.Base64;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.filtering.events.EventSpec;
import com.example.demo.model.Event;
import com.example.demo.repository.EventRepository;

@Service
public class EventService implements ServiceInterface<EventRequest, EventResponse, EventViewResponse, EventSpec> {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public EventService(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public EventResponse create(EventRequest event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public Page<EventViewResponse> getAll(EventSpec spec, Pageable pageable) {
        Page<Event> eventsPage = eventRepository.findAll(spec, pageable);

        return eventsPage.map(event -> modelMapper.map(event, EventViewResponse.class));
    }

    public EventViewResponse updateEvent(Integer eventId, EventRequest eventRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        // TODO: I will change the exception to a custom one later

        Event updatedEvent = modelMapper.map(eventRequest, Event.class);

        byte[] poster = null;
        if (eventRequest.getImageBase64() != null && !eventRequest.getImageBase64().isEmpty()) {
            poster = Base64.getDecoder().decode(eventRequest.getImageBase64());
        }

        updatedEvent.setId(event.getId());
        updatedEvent.setImage(poster);
        updatedEvent = eventRepository.save(updatedEvent);

        return modelMapper.map(updatedEvent, EventViewResponse.class);
    }

    public EventDetailsResponse getById(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event", id));

        EventDetailsResponse response = modelMapper.map(event, EventDetailsResponse.class);

        response.setImage(event.getImage() != null
                ? Base64.getEncoder().encodeToString(event.getImage())
                : null);

        return response;
    }

}
