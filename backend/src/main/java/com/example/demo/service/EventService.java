package com.example.demo.service;

import com.example.demo.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import com.example.demo.model.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;
import java.time.LocalDateTime;
import java.util.Base64;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.MissingLocationException;
import com.example.demo.filtering.events.EventSpec;
import com.example.demo.model.Event;
import com.example.demo.model.EventStatus;
import com.example.demo.repository.EventRepository;
import com.example.demo.exceptions.EventCannotBeCompletedException;
import com.example.demo.model.EventType;
import com.example.demo.model.User;
import org.springframework.dao.DataAccessResourceFailureException;

@RequiredArgsConstructor
@Service
public class EventService implements EventServiceInterface {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final Base64.Decoder decoder = Base64.getDecoder();
    private final Base64.Encoder encoder = Base64.getEncoder();

    private void validateEvent(EventRequest eventRequest) {
        if (eventRequest.getType() != EventType.INTERNAL && eventRequest.getLocation() == null) {
            throw new MissingLocationException("Event location is required for non-internal events.");
        }
    }

    @Override
    public EventResponse create(EventRequest eventRequest, String username) {
        validateEvent(eventRequest);

        Event event = modelMapper.map(eventRequest, Event.class);

        User user;
        try {
            user = userService.findByEmail(username);
        } catch (DataAccessResourceFailureException exception) {
            throw new NotFoundException(username, null);
        }

        event.setStatus(EventStatus.DRAFT);
        event.setCreatedBy(user);
        eventRepository.save(event);
        return modelMapper.map(event, EventResponse.class);
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
        if (eventRequest.getImage() != null && !eventRequest.getImage().isEmpty()) {
            poster = decoder.decode(eventRequest.getImage());
        }

        updatedEvent.setId(event.getId());
        updatedEvent.setImage(poster);
        updatedEvent.setStatus(EventStatus.DRAFT);
        updatedEvent = eventRepository.save(updatedEvent);

        return modelMapper.map(updatedEvent, EventViewResponse.class);
    }

    public EventDetailsResponse getById(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event", id));

        EventDetailsResponse response = modelMapper.map(event, EventDetailsResponse.class);

        response.setImage(event.getImage() != null
                ? encoder.encodeToString(event.getImage())
                : null);

        return response;
    }

    public EventDetailsResponse completeEvent(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event", id));

        if (event.getStatus() == EventStatus.COMPLETED) {
            throw new EventCannotBeCompletedException(
                    "Event is already completed.");
        }

        if (event.getEndTime().isAfter(LocalDateTime.now())) {
            throw new EventCannotBeCompletedException(
                    "Event cannot be completed before its end time.");
        }

        event.setStatus(EventStatus.COMPLETED);

        Event updatedEvent = eventRepository.save(event);

        EventDetailsResponse response = modelMapper.map(updatedEvent, EventDetailsResponse.class);

        response.setImage(updatedEvent.getImage() != null
                ? encoder.encodeToString(updatedEvent.getImage())
                : null);

        return response;
    }

    public EventResponse publishEvent(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event", id));
        event.setStatus(EventStatus.PUBLISHED);
        eventRepository.save(event);
        return modelMapper.map(event, EventResponse.class);
    }
}
