package com.example.demo.service;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.filtering.events.EventSpec;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verifyNoInteractions;

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
    void createEvent_WhenTypeIsLocal_SetsLocationAndFoodProvided() {
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
    void createEvent_WhenTypeIsInternal_ForcesLocationAllAndKeepsFoodProvided() {
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
    void createEvent_WhenTypeIsExternal_SetsLocationAndLeavesFoodProvidedNull() {
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
    void createEvent_WhenUserIsMissing_SavesEventWithNullCreator() {
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
    void createEvent_WhenRepositorySaveFails_PropagatesException() {
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

    @Test
    void getEvents_WhenEventsExist_ReturnsMappedPage() {
        Event event = new Event();
        EventViewResponse viewResponse = new EventViewResponse();
        viewResponse.setId(1);
        viewResponse.setName("Team event");
        EventSpec spec = mock(EventSpec.class);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Event> eventsPage = new PageImpl<>(List.of(event), pageable, 1);

        when(eventRepository.findAll(spec, pageable)).thenReturn(eventsPage);
        when(modelMapper.map(event, EventViewResponse.class)).thenReturn(viewResponse);

        Page<EventViewResponse> result = eventService.getAll(spec, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(viewResponse, result.getContent().get(0));
        assertEquals(pageable, result.getPageable());
        verify(modelMapper).map(event, EventViewResponse.class);
    }

    @Test
    void getEvents_WhenNoEventsMatch_ReturnsEmptyPageWithoutMapping() {
        EventSpec spec = mock(EventSpec.class);
        Pageable pageable = PageRequest.of(0, 20);

        when(eventRepository.findAll(spec, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        Page<EventViewResponse> result = eventService.getAll(spec, pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verifyNoInteractions(modelMapper);
    }

    @Test
    void getEvents_WhenCalledWithSpecAndPageable_PassesThemToRepository() {
        EventSpec spec = mock(EventSpec.class);
        Pageable pageable = PageRequest.of(2, 5);

        when(eventRepository.findAll(spec, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        eventService.getAll(spec, pageable);

        ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(eventRepository).findAll(specCaptor.capture(), pageableCaptor.capture());

        assertEquals(spec, specCaptor.getValue());
        assertEquals(pageable, pageableCaptor.getValue());
    }

    @Test
    void getEvents_WhenSpecIsNull_QueriesRepositoryWithoutFilters() {
        Pageable pageable = PageRequest.of(0, 20);

        when(eventRepository.findAll((EventSpec) isNull(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        Page<EventViewResponse> result = eventService.getAll(null, pageable);

        assertTrue(result.getContent().isEmpty());
        verify(eventRepository).findAll((EventSpec) isNull(), eq(pageable));
    }

    @Test
    void getEvents_WhenRepositoryFails_PropagatesException() {
        EventSpec spec = mock(EventSpec.class);
        Pageable pageable = PageRequest.of(0, 20);

        when(eventRepository.findAll(spec, pageable))
                .thenThrow(new DataAccessResourceFailureException("Database unavailable"));

        assertThrows(DataAccessResourceFailureException.class, () -> eventService.getAll(spec, pageable));
    }
}
