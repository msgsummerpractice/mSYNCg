package com.example.demo.service;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.model.Event;
import com.example.demo.model.EventStatus;
import com.example.demo.model.EventType;
import com.example.demo.model.Location;
import com.example.demo.model.User;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createEventWhenTypeIsLocalSetsLocationAndFoodProvided() {
        EventRequest request = buildValidRequest(EventType.LOCAL);
        EventResponse mappedResponse = new EventResponse(1L, "DRAFT");
        User creator = new User();
        creator.setEmail("organizer@example.com");

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("organizer@example.com", "password"));
        when(userRepository.findByEmail("organizer@example.com")).thenReturn(creator);
        when(modelMapper.map(any(Event.class), any())).thenReturn(mappedResponse);

        EventResponse response = eventService.create(request);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        assertNotNull(response);
        assertEquals(EventStatus.DRAFT, savedEvent.getStatus());
        assertEquals(EventType.LOCAL, savedEvent.getType());
        assertEquals(Location.CLUJ_NAPOCA, savedEvent.getLocation());
        assertEquals(Boolean.TRUE, savedEvent.getFoodProvided());
        assertEquals(creator, savedEvent.getCreatedBy());
        assertNotNull(savedEvent.getCreatedAt());
    }

    @Test
    void createEventWhenTypeIsInternalForcesLocationAllAndKeepsFoodProvided() {
        EventRequest request = buildValidRequest(EventType.INTERNAL);
        request.setEventLocation(Location.TIMISOARA);
        request.setFoodProvided(false);
        EventResponse mappedResponse = new EventResponse(2L, "DRAFT");

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("organizer@example.com", "password"));
        when(userRepository.findByEmail("organizer@example.com")).thenReturn(new User());
        when(modelMapper.map(any(Event.class), any())).thenReturn(mappedResponse);

        eventService.create(request);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        assertEquals(EventType.INTERNAL, savedEvent.getType());
        assertEquals(Location.ALL, savedEvent.getLocation());
        assertEquals(Boolean.FALSE, savedEvent.getFoodProvided());
    }

    @Test
    void createEventWhenTypeIsExternalSetsLocationAndLeavesFoodProvidedNull() {
        EventRequest request = buildValidRequest(EventType.EXTERNAL);
        EventResponse mappedResponse = new EventResponse(3L, "DRAFT");

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("organizer@example.com", "password"));
        when(userRepository.findByEmail("organizer@example.com")).thenReturn(new User());
        when(modelMapper.map(any(Event.class), any())).thenReturn(mappedResponse);

        eventService.create(request);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();

        assertEquals(EventType.EXTERNAL, savedEvent.getType());
        assertEquals(Location.CLUJ_NAPOCA, savedEvent.getLocation());
        assertNull(savedEvent.getFoodProvided());
    }

    @Test
    void createEventWhenUserIsMissingSavesEventWithNullCreator() {
        EventRequest request = buildValidRequest(EventType.LOCAL);
        EventResponse mappedResponse = new EventResponse(4L, "DRAFT");

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("unknown@example.com", "password"));
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);
        when(modelMapper.map(any(Event.class), any())).thenReturn(mappedResponse);

        eventService.create(request);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());

        assertNull(eventCaptor.getValue().getCreatedBy());
    }

    @Test
    void createEventWhenRepositorySaveFailsPropagatesException() {
        EventRequest request = buildValidRequest(EventType.LOCAL);

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("organizer@example.com", "password"));
        when(userRepository.findByEmail("organizer@example.com")).thenReturn(new User());
        when(eventRepository.save(any(Event.class)))
                .thenThrow(new DataAccessResourceFailureException("Database unavailable"));

        assertThrows(DataAccessResourceFailureException.class, () -> eventService.create(request));
    }

    private EventRequest buildValidRequest(EventType type) {
        EventRequest request = new EventRequest();
        request.setEventName("Engineering Meetup");
        request.setEventType(type);
        request.setEventLocation(Location.CLUJ_NAPOCA);
        request.setStartTime(LocalDateTime.of(2026, 9, 1, 9, 0));
        request.setEndTime(LocalDateTime.of(2026, 9, 1, 12, 0));
        request.setRegistrationStartTime(LocalDateTime.of(2026, 8, 15, 9, 0));
        request.setRegistrationEndTime(LocalDateTime.of(2026, 8, 31, 18, 0));
        request.setDescription("Internal event for the engineering team");
        request.setFoodProvided(true);
        request.setPoster(new byte[] { 1, 2, 3 });
        return request;
    }
}
