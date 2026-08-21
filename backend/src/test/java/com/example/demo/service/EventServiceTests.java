package com.example.demo.service;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.EventCannotBeCompletedException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.filtering.events.EventSpec;
import com.example.demo.model.Event;
import com.example.demo.model.EventStatus;
import com.example.demo.model.EventType;
import com.example.demo.model.Location;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.EventRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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

	private EventRequest createEvenetRequest(String name) {
		return createEvenetRequest(name, null);
	}

	private EventRequest createEvenetRequest(String name, String imageBase64) {
		EventRequest request = new EventRequest();
		request.setName(name);
		request.setImage(imageBase64);
		return request;
	}

	private EventRequest createEvenetRequest(EventType type) {
		byte[] pngImage = {
				(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
				(byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A,
				1, 2, 3
		};

		EventRequest request = createEvenetRequest("Engineering Meetup",
				Base64.getEncoder().encodeToString(pngImage));
		request.setType(type);
		request.setLocation(Location.CLUJ_NAPOCA);
		request.setStartTime(LocalDateTime.of(2026, 9, 1, 9, 0));
		request.setEndTime(LocalDateTime.of(2026, 9, 1, 12, 0));
		request.setRegistrationStart(LocalDateTime.of(2026, 8, 15, 9, 0));
		request.setRegistrationEnd(LocalDateTime.of(2026, 8, 31, 18, 0));
		request.setDescription("Internal event for the engineering team");
		request.setFoodProvided(true);
		return request;
	}

	private Event createEvent(Integer id) {
		return createEvent(id, "Team event");
	}

	private Event createEvent(Integer id, String name) {
		Event event = new Event();
		event.setId(id);
		event.setName(name);
		return event;
	}

	private Event createEvent(Integer id, EventStatus status, LocalDateTime endTime) {
		Event event = createEvent(id);
		event.setStatus(status);
		event.setEndTime(endTime);
		return event;
	}

	private EventDetailsResponse createDetailsResponse(Integer id, String name) {
		EventDetailsResponse detailsResponse = new EventDetailsResponse();
		detailsResponse.setId(id);
		detailsResponse.setName(name);
		return detailsResponse;
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

		Page<EventViewResponse> result = eventService.getAll(spec, pageable, null);

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
		EventRequest request = createEvenetRequest(EventType.LOCAL);
		EventResponse mappedResponse = new EventResponse(1L, "DRAFT");
		User creator = new User();
		creator.setEmail("organizer@example.com");

		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken("organizer@example.com",
						"password"));
		when(userService.findByEmail("organizer@example.com")).thenReturn(creator);
		when(userService.findByEmail("organizer@example.com")).thenReturn(creator);

		Event mappedEvent = createEvent(null, request.getName());
		mappedEvent.setType(request.getType());
		mappedEvent.setLocation(request.getLocation());
		mappedEvent.setStartTime(request.getStartTime());
		mappedEvent.setEndTime(request.getEndTime());
		mappedEvent.setFoodProvided(request.getFoodProvided());
		when(modelMapper.map(request, Event.class)).thenReturn(mappedEvent);

		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
		when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(modelMapper.map(any(Event.class), eq(EventResponse.class)))
				.thenReturn(mappedResponse);

		EventResponse response = eventService.create(request, creator.getEmail());

		verify(eventRepository).save(eventCaptor.capture());
		Event savedEvent = eventCaptor.getValue();

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

		Page<EventViewResponse> result = eventService.getAll(spec, pageable, null);

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

		eventService.getAll(spec, pageable, null);

		ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(eventRepository).findAll(specCaptor.capture(), pageableCaptor.capture());

		assertEquals(spec, specCaptor.getValue());
		assertEquals(pageable, pageableCaptor.getValue());
	}

	@Test
	@SuppressWarnings("unchecked")
	void getEvents_whenUserIsParticipant_filtersByPublishedStatusOpenRegistrationAndLocation() {
		EventSpec spec = (root, query, criteriaBuilder) -> null;
		Pageable pageable = PageRequest.of(0, 20);

		when(userService.findById(7)).thenReturn(buildUser(UserRole.PARTICIPANT, Location.CLUJ_NAPOCA));
		when(eventRepository.findAll(any(Specification.class), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		eventService.getAll(spec, pageable, 7);

		Root<Event> root = mock(Root.class);
		Path<Object> statusPath = mock(Path.class);
		Path<Object> locationPath = mock(Path.class);
		Path<LocalDateTime> registrationEndPath = mock(Path.class);
		CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
		when(root.<Object>get("status")).thenReturn(statusPath);
		when(root.<Object>get("location")).thenReturn(locationPath);
		when(root.<LocalDateTime>get("registrationEnd")).thenReturn(registrationEndPath);

		LocalDateTime beforeCall = LocalDateTime.now();
		captureSpecPassedToRepository(pageable).toPredicate(root, mock(CriteriaQuery.class), criteriaBuilder);
		LocalDateTime afterCall = LocalDateTime.now();

		verify(criteriaBuilder).equal(statusPath, EventStatus.PUBLISHED);
		verify(locationPath).in(Location.CLUJ_NAPOCA, Location.ALL);

		ArgumentCaptor<LocalDateTime> nowCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		verify(criteriaBuilder).greaterThanOrEqualTo(eq(registrationEndPath), nowCaptor.capture());
		assertFalse(nowCaptor.getValue().isBefore(beforeCall));
		assertFalse(nowCaptor.getValue().isAfter(afterCall));
	}

	@Test
	@SuppressWarnings("unchecked")
	void getEvents_whenParticipantHasNoLocation_doesNotFilterByLocation() {
		EventSpec spec = (root, query, criteriaBuilder) -> null;
		Pageable pageable = PageRequest.of(0, 20);

		when(userService.findById(7)).thenReturn(buildUser(UserRole.PARTICIPANT, null));
		when(eventRepository.findAll(any(Specification.class), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		eventService.getAll(spec, pageable, 7);

		Root<Event> root = mock(Root.class);
		CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

		captureSpecPassedToRepository(pageable).toPredicate(root, mock(CriteriaQuery.class), criteriaBuilder);

		verify(root, never()).get("location");
		verify(root).get("status");
		verify(root).get("registrationEnd");
	}

	@Test
	@SuppressWarnings("unchecked")
	void getEvents_whenUserIsParticipant_appliesEligibilitySpec() {
		EventSpec spec = (root, query, criteriaBuilder) -> null;
		Pageable pageable = PageRequest.of(0, 20);

		when(userService.findById(7)).thenReturn(buildUser(UserRole.PARTICIPANT, Location.CLUJ_NAPOCA));
		when(eventRepository.findAll(any(Specification.class), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		eventService.getAll(spec, pageable, 7);

		assertNotSame(spec, captureSpecPassedToRepository(pageable));
	}

	@Test
	@SuppressWarnings("unchecked")
	void getEvents_whenParticipantAndSpecIsNull_usesEligibilitySpecAlone() {
		Pageable pageable = PageRequest.of(0, 20);

		when(userService.findById(7)).thenReturn(buildUser(UserRole.PARTICIPANT, Location.CLUJ_NAPOCA));
		when(eventRepository.findAll(any(Specification.class), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		eventService.getAll(null, pageable, 7);

		assertNotNull(captureSpecPassedToRepository(pageable));
	}

	@Test
	@SuppressWarnings("unchecked")
	void getEvents_whenUserIsNotParticipant_usesProvidedSpecOnly() {
		EventSpec spec = (root, query, criteriaBuilder) -> null;
		Pageable pageable = PageRequest.of(0, 20);

		when(userService.findById(9)).thenReturn(buildUser(UserRole.ADMIN, Location.CLUJ_NAPOCA));
		when(eventRepository.findAll(any(Specification.class), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		eventService.getAll(spec, pageable, 9);

		assertSame(spec, captureSpecPassedToRepository(pageable));
	}

	@Test
	void getEvents_whenUserIdIsNull_doesNotLookUpUser() {
		EventSpec spec = mock(EventSpec.class);
		Pageable pageable = PageRequest.of(0, 20);

		when(eventRepository.findAll(spec, pageable))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		eventService.getAll(spec, pageable, null);

		verifyNoInteractions(userService);
	}

	@Test
	void getEvents_whenUserDoesNotExist_propagatesNotFoundException() {
		EventSpec spec = mock(EventSpec.class);
		Pageable pageable = PageRequest.of(0, 20);

		when(userService.findById(404)).thenThrow(new NotFoundException("User", 404));

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> eventService.getAll(spec, pageable, 404));

		assertEquals("User with id 404 not found", exception.getMessage());
		verifyNoInteractions(eventRepository);
	}

	private User buildUser(UserRole role, Location location) {
		User user = new User();
		user.setRole(role);
		user.setLocation(location);
		return user;
	}

	private Specification<Event> captureSpecPassedToRepository(Pageable pageable) {
		ArgumentCaptor<Specification<Event>> specCaptor = ArgumentCaptor.captor();
		verify(eventRepository).findAll(specCaptor.capture(), eq(pageable));
		return specCaptor.getValue();
	}

	@Test
	void createEvent_WhenTypeIsInternal_ForcesLocationAllAndKeepsFoodProvided() {
		EventRequest request = createEvenetRequest(EventType.INTERNAL);
		request.setLocation(Location.ALL);
		request.setFoodProvided(false);
		EventResponse mappedResponse = new EventResponse(2L, "DRAFT");

		User creator = new User();
		creator.setEmail("organizer@example.com");
		Event mappedEvent = buildMappedEvent(request);
		when(modelMapper.map(request, Event.class)).thenReturn(mappedEvent);

		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken("organizer@example.com",
						"password"));
		when(userService.findByEmail("organizer@example.com")).thenReturn(creator);
		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
		when(modelMapper.map(eventCaptor.capture(), eq(EventResponse.class))).thenReturn(mappedResponse);

		eventService.create(request, "organizer@example.com");

		ArgumentCaptor<Event> eventCaptor1 = ArgumentCaptor.forClass(Event.class);
		verify(eventRepository).save(eventCaptor1.capture());
		Event savedEvent = eventCaptor1.getValue();

		assertEquals(EventType.INTERNAL, savedEvent.getType());
		assertEquals(Location.ALL, savedEvent.getLocation());
		assertEquals(Boolean.FALSE, savedEvent.getFoodProvided());
		assertEquals(EventStatus.DRAFT, savedEvent.getStatus());
		assertEquals(creator, savedEvent.getCreatedBy());
	}

	@Test
	void getEvents_whenSpecIsNull_queriesRepositoryWithoutFilters() {
		Pageable pageable = PageRequest.of(0, 20);

		when(eventRepository.findAll((EventSpec) isNull(), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		Page<EventViewResponse> result = eventService.getAll(null, pageable, null);

		assertTrue(result.getContent().isEmpty());
		verify(eventRepository).findAll((EventSpec) isNull(), eq(pageable));
	}

	@Test
	void createEvent_WhenTypeIsExternal_SetsLocationAndLeavesFoodProvidedNull() {
		EventRequest request = createEvenetRequest(EventType.EXTERNAL);
		request.setFoodProvided(null);
		EventResponse mappedResponse = new EventResponse(3L, "DRAFT");

		User creator = new User();
		creator.setEmail("organizer@example.com");
		Event mappedEvent = buildMappedEvent(request);
		when(modelMapper.map(request, Event.class)).thenReturn(mappedEvent);
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken("organizer@example.com",
						"password"));
		when(userService.findByEmail("organizer@example.com")).thenReturn(creator);
		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
		when(modelMapper.map(any(Event.class), eq(EventResponse.class))).thenReturn(mappedResponse);

		eventService.create(request, "organizer@example.com");

		verify(eventRepository).save(eventCaptor.capture());
		Event savedEvent = eventCaptor.getValue();
		assertEquals(EventType.EXTERNAL, savedEvent.getType());
		assertEquals(Location.CLUJ_NAPOCA, savedEvent.getLocation());
		assertNull(savedEvent.getFoodProvided());
		assertEquals(EventStatus.DRAFT, savedEvent.getStatus());
		assertEquals(creator, savedEvent.getCreatedBy());
	}

	@Test
	void getEvents_whenRepositoryFails_propagatesException() {
		EventSpec spec = mock(EventSpec.class);
		Pageable pageable = PageRequest.of(0, 20);

		when(eventRepository.findAll(spec, pageable))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		assertThrows(DataAccessResourceFailureException.class, () -> eventService.getAll(spec, pageable, null));
	}

	@Test
	void createEvent_WhenUserIsMissing_SavesEventWithNullCreator() {
		EventRequest request = createEvenetRequest(EventType.LOCAL);
		EventResponse mappedResponse = new EventResponse(4L, "DRAFT");
		Event mappedEvent = buildMappedEvent(request);
		when(modelMapper.map(request, Event.class)).thenReturn(mappedEvent);
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken("unknown@example.com",
						"password"));
		when(userService.findByEmail("unknown@example.com")).thenReturn(null);
		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
		when(modelMapper.map(any(Event.class), eq(EventResponse.class))).thenReturn(mappedResponse);

		eventService.create(request, "unknown@example.com");

		verify(eventRepository).save(eventCaptor.capture());

		assertNull(eventCaptor.getValue().getCreatedBy());
	}

	@Test
	void createEvent_WhenUserLookupFails_ThrowsNotFoundException() {
		EventRequest request = createEvenetRequest(EventType.LOCAL);
		String username = "organizer@example.com";

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
		EventRequest request = createEvenetRequest(EventType.LOCAL);
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
		EventRequest request = createEvenetRequest(EventType.LOCAL);

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
	void getById_whenEventExists_returnsMappedDetails() {
		Event event = createEvent(1);

		EventDetailsResponse detailsResponse = createDetailsResponse(1, "Team event");

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
		Event event = createEvent(1);
		event.setImage(Base64.getDecoder().decode(image));

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(modelMapper.map(event, EventDetailsResponse.class))
				.thenReturn(new EventDetailsResponse());

		EventDetailsResponse result = eventService.getById(1);

		assertEquals(image, result.getImage());
	}

	@Test
	void getById_whenEventHasNoImage_returnsNullImage() {
		Event event = createEvent(1);

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
	void publishEvent_whenEventExists_publishesAndSavesEvent() {
		Event event = createEvent(1);
		event.setStatus(EventStatus.DRAFT);
		EventResponse response = new EventResponse(1L, EventStatus.PUBLISHED.name());
		when(eventRepository.findById(1)).thenReturn(Optional.of(event));
		when(modelMapper.map(event, EventResponse.class)).thenReturn(response);

		EventResponse result = eventService.publishEvent(1);

		assertEquals(response, result);
		assertEquals(EventStatus.PUBLISHED, event.getStatus());
		verify(eventRepository).save(event);
		verify(modelMapper).map(event, EventResponse.class);
	}

	@Test
	void publishEvent_whenEventDoesNotExist_throwsNotFoundException() {
		when(eventRepository.findById(99)).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class, () -> eventService.publishEvent(99));

		assertEquals("Event with id 99 not found", exception.getMessage());
	}

	@Test
	void publishEvent_whenRepositoryFails_propagatesException() {
		when(eventRepository.findById(1))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		assertThrows(DataAccessResourceFailureException.class, () -> eventService.publishEvent(1));
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
		Event event = createEvent(1, EventStatus.COMPLETED, LocalDateTime.now().minusDays(1));

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));

		EventCannotBeCompletedException exception = assertThrows(
				EventCannotBeCompletedException.class,
				() -> eventService.completeEvent(1));

		assertEquals("Event is already completed.", exception.getMessage());
		verify(eventRepository, never()).save(any(Event.class));
	}

	@Test
	void completeEvent_whenEndTimeIsInFuture_throwsEventCannotBeCompletedException() {
		Event event = createEvent(1, EventStatus.PUBLISHED, LocalDateTime.now().plusDays(1));

		when(eventRepository.findById(1)).thenReturn(Optional.of(event));

		EventCannotBeCompletedException exception = assertThrows(
				EventCannotBeCompletedException.class,
				() -> eventService.completeEvent(1));

		assertEquals("Event cannot be completed before its end time.", exception.getMessage());
		verify(eventRepository, never()).save(any(Event.class));
	}

	@Test
	void completeEvent_whenEventEnded_setsStatusToCompleted() {
		Event event = createEvent(1, EventStatus.PUBLISHED, LocalDateTime.now().minusDays(1));

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
