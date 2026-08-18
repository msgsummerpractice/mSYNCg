package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import com.example.demo.model.EventStatus;
import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.MissingLocationException;
import com.example.demo.repository.EventRepository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class EventService implements ServiceInterface<EventRequest, EventResponse, EventViewResponse, EventSpec> {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public EventResponse create(EventRequest eventRequest) {

        Event event = new Event();
        event.setName(eventRequest.getEventName());
        event.setCreatedAt(LocalDateTime.now());
        event.setType(eventRequest.getEventType());
        event.setStartTime(eventRequest.getStartTime());
        event.setEndTime(eventRequest.getEndTime());
        event.setRegistrationStart(eventRequest.getRegistrationStartTime());
        event.setRegistrationEnd(eventRequest.getRegistrationEndTime());
        event.setDescription(eventRequest.getDescription());
        event.setStatus(EventStatus.DRAFT);
        event.setImage(eventRequest.getPoster());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByEmail(username);

        event.setCreatedBy(user);
        if(eventRequest.getEventType() != null){
            if(eventRequest.getEventLocation() == null && eventRequest.getEventType() != EventType.INTERNAL) {
                throw new MissingLocationException("Event location is required for non-internal events.");
            }
        }
        if (eventRequest.getEventType() == EventType.LOCAL) {
            event.setLocation(eventRequest.getEventLocation());
            event.setFoodProvided(eventRequest.getFoodProvided());
        } else if (eventRequest.getEventType() == EventType.INTERNAL) {
            event.setLocation(Location.ALL);
            event.setFoodProvided(eventRequest.getFoodProvided());
        } else if (eventRequest.getEventType() == EventType.EXTERNAL) {
            event.setLocation(eventRequest.getEventLocation());
        }

        eventRepository.save(event);
        return modelMapper.map(event, EventResponse.class);
    }


    @Override
    public Page<EventViewResponse> getAll(EventSpec spec, Pageable pageable) {
        Page<Event> eventsPage = eventRepository.findAll(spec, pageable);

        return eventsPage.map(event -> modelMapper.map(event, EventViewResponse.class));
    }

}
