package com.example.demo.controller;

import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.EventCannotBeCompletedException;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.filtering.events.EventSpec;
import com.example.demo.model.EventStatus;
import com.example.demo.model.EventType;
import com.example.demo.model.Location;
import com.example.demo.service.EventService;
import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventControllerTests {

	@Mock
	private EventService eventService;

	@InjectMocks
	private EventController eventController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(eventController)
				.setCustomArgumentResolvers(
						new SpecificationArgumentResolver(),
						new PageableHandlerMethodArgumentResolver())
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	void getEvents_whenNoFilters_returnsPageOfEvents() throws Exception {
		Page<EventViewResponse> page = new PageImpl<>(List.of(buildViewResponse()), PageRequest.of(0, 20), 1);

		ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		when(eventService.getAll(specCaptor.capture(), pageableCaptor.capture())).thenReturn(page);

		mockMvc.perform(get("/api/events"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(1))
				.andExpect(jsonPath("$.content[0].name").value("Team event"))
				.andExpect(jsonPath("$.content[0].status").value(EventStatus.PUBLISHED.name()))
				.andExpect(jsonPath("$.totalElements").value(1));

		assertNotNull(specCaptor.getValue());
		assertNotNull(pageableCaptor.getValue());
	}

	@Test
	void getEvents_whenNoResults_returnsEmptyPage() throws Exception {
		ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		when(eventService.getAll(specCaptor.capture(), pageableCaptor.capture()))
				.thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

		mockMvc.perform(get("/api/events").param("name", "Nobody"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isEmpty())
				.andExpect(jsonPath("$.totalElements").value(0));

		assertNotNull(specCaptor.getValue());
		assertNotNull(pageableCaptor.getValue());
	}

	@Test
	void getEvents_whenPaginationParamsProvided_forwardsPageableToService() throws Exception {
		ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		when(eventService.getAll(specCaptor.capture(), pageableCaptor.capture()))
				.thenReturn(new PageImpl<>(List.of(), PageRequest.of(2, 5), 0));

		mockMvc.perform(get("/api/events")
				.param("page", "2")
				.param("size", "5"))
				.andExpect(status().isOk());

		assertNotNull(specCaptor.getValue());
		assertEquals(2, pageableCaptor.getValue().getPageNumber());
		assertEquals(5, pageableCaptor.getValue().getPageSize());
	}

	@Test
	void getEvents_whenFiltersProvided_resolvesEventSpec() throws Exception {
		Page<EventViewResponse> page = new PageImpl<>(List.of(buildViewResponse()), PageRequest.of(0, 20), 1);

		ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		when(eventService.getAll(specCaptor.capture(), pageableCaptor.capture())).thenReturn(page);

		mockMvc.perform(get("/api/events")
				.param("name", "Team")
				.param("status", EventStatus.PUBLISHED.name()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value("Team event"));

		assertNotNull(specCaptor.getValue());
		assertNotNull(pageableCaptor.getValue());
	}

	@Test
	void getEvents_whenServiceThrowsUnexpectedException_returns_internalServerError() throws Exception {
		ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		when(eventService.getAll(specCaptor.capture(), pageableCaptor.capture()))
				.thenThrow(new RuntimeException("Database unavailable"));

		mockMvc.perform(get("/api/events"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.error").value("Internal Server Error"));

		assertNotNull(specCaptor.getValue());
		assertNotNull(pageableCaptor.getValue());
	}

	private EventViewResponse buildViewResponse() {
		return new EventViewResponse(1, "Team event", null, EventStatus.PUBLISHED,
				EventType.EXTERNAL, Location.CLUJ_NAPOCA);
	}

	@Test
	void getEventDetails_whenEventExists_returnsEventDetails() throws Exception {
		EventDetailsResponse details = buildDetailsResponse();
		details.setImage("AQIDBA==");

		when(eventService.getById(1)).thenReturn(details);

		mockMvc.perform(get("/api/events/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Team event"))
				.andExpect(jsonPath("$.status").value(EventStatus.PUBLISHED.name()))
				.andExpect(jsonPath("$.type").value(EventType.EXTERNAL.name()))
				.andExpect(jsonPath("$.location").value("Cluj-Napoca"))
				.andExpect(jsonPath("$.image").value("AQIDBA=="));

		verify(eventService).getById(1);
	}

	@Test
	void getEventDetails_whenEventHasNoImage_returnsNullImage() throws Exception {
		when(eventService.getById(1)).thenReturn(buildDetailsResponse());

		mockMvc.perform(get("/api/events/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.image").doesNotExist());
	}

	@Test
	void getEventDetails_whenEventDoesNotExist_returnsNotFound() throws Exception {
		when(eventService.getById(99)).thenThrow(new NotFoundException("Event", 99));

		mockMvc.perform(get("/api/events/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Not Found"))
				.andExpect(jsonPath("$.message").value("Event with id 99 not found"));
	}

	@Test
	void getEventDetails_whenIdIsNotANumber_returns_internalServerError() throws Exception {
		mockMvc.perform(get("/api/events/abc"))
				.andExpect(status().isInternalServerError());

		verifyNoInteractions(eventService);
	}

	@Test
	void getEventDetails_whenServiceThrowsUnexpectedException_returnsInternalServerError() throws Exception {
		when(eventService.getById(1)).thenThrow(new RuntimeException("Database unavailable"));

		mockMvc.perform(get("/api/events/1"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.error").value("Internal Server Error"));
	}

	private EventDetailsResponse buildDetailsResponse() {
		EventDetailsResponse details = new EventDetailsResponse();
		details.setId(1);
		details.setName("Team event");
		details.setStatus(EventStatus.PUBLISHED);
		details.setType(EventType.EXTERNAL);
		details.setLocation(Location.CLUJ_NAPOCA);
		details.setFoodProvided(true);
		details.setDescription("Yearly team gathering");
		return details;
	}

	@Test
	void completeEvent_whenSuccessful_returnsCompletedEvent() throws Exception {
		EventDetailsResponse completedEvent = buildDetailsResponse();
		completedEvent.setStatus(EventStatus.COMPLETED);

		when(eventService.completeEvent(1)).thenReturn(completedEvent);

		mockMvc.perform(patch("/api/events/1/complete"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.status").value(EventStatus.COMPLETED.name()));

		verify(eventService).completeEvent(1);
	}

	@Test
	void completeEvent_whenEventDoesNotExist_returnsNotFound() throws Exception {
		when(eventService.completeEvent(99)).thenThrow(new NotFoundException("Event", 99));

		mockMvc.perform(patch("/api/events/99/complete"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Not Found"))
				.andExpect(jsonPath("$.message").value("Event with id 99 not found"));
	}

	@Test
	void completeEvent_whenEventCannotBeCompleted_returnsBadRequest() throws Exception {
		when(eventService.completeEvent(1))
				.thenThrow(new EventCannotBeCompletedException("Event cannot be completed before its end time."));

		mockMvc.perform(patch("/api/events/1/complete"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Bad Request"))
				.andExpect(jsonPath("$.message").value("Event cannot be completed before its end time."));
	}
}
