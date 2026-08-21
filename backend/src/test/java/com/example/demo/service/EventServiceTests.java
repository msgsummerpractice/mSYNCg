package com.example.demo.service;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.EventCannotBeCompletedException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.filtering.events.EventSpec;
import com.example.demo.model.Event;
import com.example.demo.model.EventStatus;
import com.example.demo.model.EventType;
import com.example.demo.model.Location;
import com.example.demo.model.User;
import com.example.demo.repository.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private UserService userService;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private EventService eventService;

	private EventRequest createEventRequest(String name) {
		return createEventRequest(name, null);
	}

	private EventRequest createEventRequest(String name, String imageBase64) {
		EventRequest request = new EventRequest();
		request.setName(name);
		request.setImage(imageBase64);
		return request;
	}

	private Event createEvent(Integer id) {
		Event event = new Event();
		event.setId(id);
		return event;
	}

	private EventViewResponse createViewResponse(Integer id, String name) {
		EventViewResponse response = new EventViewResponse();
		response.setId(id);
		response.setName(name);
		return response;
	}

	@Test
	void getEvents_whenEventsExist_returnsMappedPage() {
		Event event = createEvent(1);
		EventViewResponse viewResponse = createViewResponse(1, "Team event");
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
				.setAuthentication(new UsernamePasswordAuthenticationToken("organizer@example.com",
						"password"));
		when(userService.findByEmail("organizer@example.com")).thenReturn(creator);
		when(userService.findByEmail("organizer@example.com")).thenReturn(creator);

		Event mappedEvent = new Event();
		when(modelMapper.map(request, Event.class)).thenReturn(mappedEvent);

		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

		when(eventRepository.save(eventCaptor.capture()))
				.thenReturn(new Event());

		when(modelMapper.map(eventCaptor.capture(), eq(EventResponse.class)))
				.thenReturn(mappedResponse);

		EventResponse response = eventService.create(request, creator.getEmail());

		verify(eventRepository).save(eventCaptor.capture());
		Event savedEvent = eventCaptor.getValue();
		savedEvent.setType(EventType.LOCAL);
		savedEvent.setLocation(Location.CLUJ_NAPOCA);
		savedEvent.setFoodProvided(true);

		assertNotNull(response);
		assertEquals(EventStatus.DRAFT, savedEvent.getStatus());
		assertEquals(EventType.LOCAL, savedEvent.getType());
		assertEquals(Location.CLUJ_NAPOCA, savedEvent.getLocation());
		assertEquals(Boolean.TRUE, savedEvent.getFoodProvided());
		assertEquals(creator, savedEvent.getCreatedBy());
	}

	@Test
	void getEvents_whenNoEventsMatch_returnsEmptyPageWithoutMapping() {
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
	void getEvents_whenCalledWithSpecAndPageable_passesThemToRepository() {
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
	void createEvent_WhenTypeIsInternal_ForcesLocationAllAndKeepsFoodProvided() {
		EventRequest request = buildValidRequest(EventType.INTERNAL);
		request.setLocation(Location.ALL);
		request.setFoodProvided(false);
		EventResponse mappedResponse = new EventResponse(2L, "DRAFT");

		Event mappedEvent = buildMappedEvent(request);
		when(modelMapper.map(request, Event.class)).thenReturn(mappedEvent);

		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken("organizer@example.com",
						"password"));
		when(userService.findByEmail("organizer@example.com")).thenReturn(new User());
		when(userService.findByEmail("organizer@example.com")).thenReturn(new User());
		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
		when(modelMapper.map(eventCaptor.capture(), eq(EventResponse.class))).thenReturn(mappedResponse);

		eventService.create(request, "organizer@example.com");

		ArgumentCaptor<Event> eventCaptor1 = ArgumentCaptor.forClass(Event.class);
		verify(eventRepository).save(eventCaptor1.capture());
		Event savedEvent = eventCaptor1.getValue();

		assertEquals(EventType.INTERNAL, savedEvent.getType());
		assertEquals(Location.ALL, savedEvent.getLocation());
		assertEquals(Boolean.FALSE, savedEvent.getFoodProvided());
	}

	@Test
	void getEvents_whenSpecIsNull_queriesRepositoryWithoutFilters() {
		Pageable pageable = PageRequest.of(0, 20);

		when(eventRepository.findAll((EventSpec) isNull(), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		Page<EventViewResponse> result = eventService.getAll(null, pageable);

		assertTrue(result.getContent().isEmpty());
		verify(eventRepository).findAll((EventSpec) isNull(), eq(pageable));
	}

	@Test
	void createEvent_WhenTypeIsExternal_SetsLocationAndLeavesFoodProvidedNull() {
		EventRequest request = buildValidRequest(EventType.EXTERNAL);
		EventResponse mappedResponse = new EventResponse(3L, "DRAFT");

		Event mappedEvent = buildMappedEvent(request);
		when(modelMapper.map(request, Event.class)).thenReturn(mappedEvent);
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken("organizer@example.com",
						"password"));
		when(userService.findByEmail("organizer@example.com")).thenReturn(new User());
		when(userService.findByEmail("organizer@example.com")).thenReturn(new User());
		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
		when(modelMapper.map(eventCaptor.capture(), eq(EventResponse.class))).thenReturn(mappedResponse);

		eventService.create(request, "organizer@example.com");

		verify(eventRepository).save(eventCaptor.capture());
		Event savedEvent = eventCaptor.getValue();
		savedEvent.setFoodProvided(null);
	}

	@Test
	void getEvents_whenRepositoryFails_propagatesException() {
		EventSpec spec = mock(EventSpec.class);
		Pageable pageable = PageRequest.of(0, 20);

		when(eventRepository.findAll(spec, pageable))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		assertThrows(DataAccessResourceFailureException.class, () -> eventService.getAll(spec, pageable));
	}

	@Test
	void createEvent_WhenUserIsMissing_ThrowsNotFoundException() {
		EventRequest request = buildValidRequest(EventType.LOCAL);
		String username = "unknown@example.com";
		when(modelMapper.map(request, Event.class)).thenReturn(buildMappedEvent(request));
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(username,
						"password"));
		when(userService.findByEmail(username))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> eventService.create(request, username));

		assertEquals(username + " with id null not found", exception.getMessage());
		verify(eventRepository, never()).save(any(Event.class));
	}

	@Test
	void createEvent_WhenUserLookupFails_PropagatesDatabaseException() {
		EventRequest request = buildValidRequest(EventType.LOCAL);
		String username = "organizer@example.com";

		when(modelMapper.map(request, Event.class)).thenReturn(buildMappedEvent(request));
		when(userService.findByEmail(username))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		assertThrows(NotFoundException.class,
				() -> eventService.create(request, username));

		verify(eventRepository, never()).save(any(Event.class));
	}

	@Test
	void createEvent_WhenRepositorySaveFails_PropagatesException() {
		EventRequest request = buildValidRequest(EventType.LOCAL);

		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken("organizer@example.com",
						"password"));
		when(modelMapper.map(request, Event.class)).thenReturn(buildMappedEvent(request));
		when(userService.findByEmail("organizer@example.com")).thenReturn(new User());
		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
		when(eventRepository.save(eventCaptor.capture()))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		assertThrows(DataAccessResourceFailureException.class,
				() -> eventService.create(request, "organizer@example.com"));
	}

	private EventRequest buildValidRequest(EventType type) {
		EventRequest request = new EventRequest();
		request.setName("Engineering Meetup");
		request.setType(type);
		request.setLocation(Location.CLUJ_NAPOCA);
		request.setStartTime(LocalDateTime.of(2026, 9, 1, 9, 0));
		request.setEndTime(LocalDateTime.of(2026, 9, 1, 12, 0));
		request.setRegistrationStart(LocalDateTime.of(2026, 8, 15, 9, 0));
		request.setRegistrationEnd(LocalDateTime.of(2026, 8, 31, 18, 0));
		request.setDescription("Internal event for the engineering team");
		request.setFoodProvided(true);
		byte[] pngImage = {
				(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
				(byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A,
				1, 2, 3
		};

		request.setImage(Base64.getEncoder().encodeToString(pngImage));
		return request;
	}

	private Event buildMappedEvent(EventRequest request) {
		Event event = new Event();
		event.setName(request.getName());
		event.setType(request.getType());
		event.setLocation(request.getLocation());
		event.setStartTime(request.getStartTime());
		event.setEndTime(request.getEndTime());
		event.setFoodProvided(request.getFoodProvided());
		event.setRegistrationStart(request.getRegistrationStart());
		event.setRegistrationEnd(request.getRegistrationEnd());
		event.setDescription(request.getDescription());
		event.setImage(request.getImage() == null || request.getImage().isEmpty()
				? null
				: Base64.getDecoder().decode(request.getImage()));
		return event;
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

	@Test
	void updateEvent_whenEventExists_updatesAndReturnsMappedResponse() {
		Integer eventId = 1;
		Event existingEvent = createEvent(eventId);
		EventRequest eventRequest = createEventRequest("Updated Event");
		Event mappedEvent = createEvent(null);
		Event savedEvent = createEvent(eventId);
		EventViewResponse viewResponse = createViewResponse(eventId, "Updated Event");

		when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.of(existingEvent));
		when(modelMapper.map(eventRequest, Event.class)).thenReturn(mappedEvent);
		when(eventRepository.save(mappedEvent)).thenReturn(savedEvent);
		when(modelMapper.map(savedEvent, EventViewResponse.class)).thenReturn(viewResponse);

		EventViewResponse result = eventService.updateEvent(eventId, eventRequest);

		assertEquals(viewResponse, result);
		verify(eventRepository).findById(eventId);
		verify(modelMapper).map(eventRequest, Event.class);
		verify(eventRepository).save(mappedEvent);
		verify(modelMapper).map(savedEvent, EventViewResponse.class);
	}

	@Test
	void updateEvent_whenEventNotFound_throwsException() {
		Integer eventId = 999;
		EventRequest eventRequest = new EventRequest();

		when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> eventService.updateEvent(eventId, eventRequest));
		verify(eventRepository).findById(eventId);
	}

	@Test
	void getById_WhenEventExists_ReturnsMappedDetails() {
		Event event = new Event();
		event.setId(1);
		event.setName("Team event");

		EventDetailsResponse detailsResponse = new EventDetailsResponse();
		detailsResponse.setId(1);
		detailsResponse.setName("Team event");

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(modelMapper.map(event, EventDetailsResponse.class)).thenReturn(detailsResponse);

		EventDetailsResponse result = eventService.getById(1);

		assertEquals(1, result.getId());
		assertEquals("Team event", result.getName());
		verify(eventRepository).findById(1);
		verify(modelMapper).map(event, EventDetailsResponse.class);
	}

	@Test
	void getById_whenEventHasImage_returnsImageString() {
		String image = "iVBORw0KGgoAAAANSUhEUg==";
		Event event = new Event();
		event.setId(1);
		event.setImage(Base64.getDecoder().decode(image));

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(modelMapper.map(event, EventDetailsResponse.class))
				.thenReturn(new EventDetailsResponse());

		EventDetailsResponse result = eventService.getById(1);

		assertEquals(image, result.getImage());
	}

	@Test
	void getById_whenEventHasNoImage_returnsNullImage() {
		Event event = new Event();
		event.setId(1);

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(modelMapper.map(event, EventDetailsResponse.class)).thenReturn(new EventDetailsResponse());

		EventDetailsResponse result = eventService.getById(1);

		assertNull(result.getImage());
	}

	@Test
	void getById_whenEventDoesNotExist_throwsNotFoundException() {
		when(eventRepository.findById(99)).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class, () -> eventService.getById(99));

		assertEquals("Event with id 99 not found", exception.getMessage());
		verifyNoInteractions(modelMapper);
	}

	@Test
	void getById_whenRepositoryFails_propagatesException() {
		when(eventRepository.findById(1))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		assertThrows(DataAccessResourceFailureException.class, () -> eventService.getById(1));
	}

	@Test
	void completeEvent_whenEventDoesNotExist_throwsNotFoundException() {
		when(eventRepository.findById(99)).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> eventService.completeEvent(99));

		assertEquals("Event with id 99 not found", exception.getMessage());
		verify(eventRepository, never()).save(any(Event.class));
	}

	@Test
	void completeEvent_whenEventAlreadyCompleted_throwsEventCannotBeCompletedException() {
		Event event = new Event();
		event.setId(1);
		event.setStatus(EventStatus.COMPLETED);
		event.setEndTime(LocalDateTime.now().minusDays(1));

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));

		EventCannotBeCompletedException exception = assertThrows(
				EventCannotBeCompletedException.class,
				() -> eventService.completeEvent(1));

		assertEquals("Event is already completed.", exception.getMessage());
		verify(eventRepository, never()).save(any(Event.class));
	}

	@Test
	void completeEvent_whenEndTimeIsInFuture_throwsEventCannotBeCompletedException() {
		Event event = new Event();
		event.setId(1);
		event.setStatus(EventStatus.PUBLISHED);
		event.setEndTime(LocalDateTime.now().plusDays(1));

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));

		EventCannotBeCompletedException exception = assertThrows(
				EventCannotBeCompletedException.class,
				() -> eventService.completeEvent(1));

		assertEquals("Event cannot be completed before its end time.", exception.getMessage());
		verify(eventRepository, never()).save(any(Event.class));
	}

	@Test
	void completeEvent_whenEventEnded_setsStatusToCompleted() {
		Event event = new Event();
		event.setId(1);
		event.setStatus(EventStatus.PUBLISHED);
		event.setEndTime(LocalDateTime.now().minusDays(1));

		EventDetailsResponse response = new EventDetailsResponse();
		response.setId(1);
		response.setStatus(EventStatus.COMPLETED);

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(eventRepository.save(event)).thenReturn(event);
		when(modelMapper.map(event, EventDetailsResponse.class)).thenReturn(response);

		EventDetailsResponse result = eventService.completeEvent(1);

		assertEquals(EventStatus.COMPLETED, event.getStatus());
		assertEquals(response, result);
		verify(eventRepository).save(event);
	}
}
