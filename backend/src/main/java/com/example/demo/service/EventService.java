package com.example.demo.service;

import com.example.demo.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import com.example.demo.model.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;

import org.modelmapper.ModelMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;
import java.util.List;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.MissingLocationException;
import com.example.demo.filtering.events.EventSpec;
import com.example.demo.model.Event;
import com.example.demo.model.EventStatus;
import com.example.demo.model.EventParticipationStatus;
import com.example.demo.model.Location;
import com.example.demo.model.RegistrationStatus;
import com.example.demo.repository.AttendanceRecordRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.RegistrationRepository;
import com.example.demo.exceptions.EventCannotBeCompletedException;
import com.example.demo.model.EventType;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import org.springframework.dao.DataAccessResourceFailureException;

@RequiredArgsConstructor
@Service
public class EventService implements EventServiceInterface {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final CheckInServiceInterface checkInService;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final RegistrationRepository registrationRepository;
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

        byte[] poster = null;
        if (eventRequest.getImage() != null && !eventRequest.getImage().isEmpty()) {
            poster = decoder.decode(eventRequest.getImage());
        }

        event.setImage(poster);
        event.setStatus(EventStatus.DRAFT);
        event.setCreatedBy(user);
        event.setImage(poster);
        event.setCreatedAt(LocalDateTime.now());
        eventRepository.save(event);
        return modelMapper.map(event, EventResponse.class);
    }

    @Override
    public Page<EventViewResponse> getAll(EventSpec spec, Pageable pageable, Integer userId) {
        Specification<Event> effectiveSpec = spec;

        if (userId != null) {
            User user = userService.findById(userId);
            if (user.getRole() == UserRole.PARTICIPANT) {
                Specification<Event> eligibility = eligibleForParticipant(user.getLocation());
                effectiveSpec = spec == null ? eligibility : spec.and(eligibility);
            }
        }

        Page<Event> eventsPage = eventRepository.findAll(effectiveSpec, pageable);

        return eventsPage.map(event -> {
            EventViewResponse response = modelMapper.map(event, EventViewResponse.class);
            
            if (userId != null) {
                response.setParticipationStatus(
                    getParticipationStatus(userId, event.getId())
                );
            }
            
            return response;
        });
    }

    private EventParticipationStatus getParticipationStatus(Integer userId, Integer eventId) {
    
        boolean hasCheckedIn = attendanceRecordRepository.existsByUserIdAndEventId(userId, eventId);
        if (hasCheckedIn) {
            return EventParticipationStatus.CHECKED_IN;
        }
        
        
        boolean isRegistered = registrationRepository.existsByUserIdAndEventIdAndStatus(
            userId, 
            eventId, 
            RegistrationStatus.REGISTERED
        );
        if (isRegistered) {
            return EventParticipationStatus.REGISTERED;
        }
        
        return null;  
    }

    private Specification<Event> eligibleForParticipant(Location participantLocation) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("status"), EventStatus.PUBLISHED));
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("registrationEnd"), LocalDateTime.now()));

            if (participantLocation != null) {
                predicates.add(root.get("location").in(participantLocation, Location.ALL));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public EventViewResponse updateEvent(Integer eventId, EventRequest eventRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId, eventId));

        Event updatedEvent = modelMapper.map(eventRequest, Event.class);

        byte[] poster = null;
        if (eventRequest.getImage() != null && !eventRequest.getImage().isEmpty()) {
            poster = decoder.decode(eventRequest.getImage());
        }

        updatedEvent.setStatus(EventStatus.DRAFT);
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
                ? encoder.encodeToString(event.getImage())
                : null);

        checkInService.getCodesForEvent(id).ifPresent(codes -> {
            response.setQrCode(codes.getQrCode());
            response.setCode(codes.getCode());
        });

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

    public Event findEventById(Integer id) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        } else {
            throw new NotFoundException("Event", id);
        }
    }
}
