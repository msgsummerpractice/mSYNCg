package com.example.demo.controller;

import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.GlobalExceptionHandler;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void getEventsWhenNoFiltersReturnsPageOfEvents() throws Exception {
	Page<EventViewResponse> page = new PageImpl<>(List.of(buildViewResponse()), PageRequest.of(0, 20), 1);

	when(eventService.getAll(any(EventSpec.class), any(Pageable.class))).thenReturn(page);

	mockMvc.perform(get("/api/events"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content[0].id").value(1))
		.andExpect(jsonPath("$.content[0].name").value("Team event"))
		.andExpect(jsonPath("$.content[0].status").value(EventStatus.PUBLISHED.name()))
		.andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getEventsWhenNoResultsReturnsEmptyPage() throws Exception {
	when(eventService.getAll(any(EventSpec.class), any(Pageable.class)))
		.thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

	mockMvc.perform(get("/api/events").param("name", "Nobody"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content").isEmpty())
		.andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getEventsWhenPaginationParamsProvidedForwardsPageableToService() throws Exception {
	when(eventService.getAll(any(EventSpec.class), any(Pageable.class)))
		.thenReturn(new PageImpl<>(List.of(), PageRequest.of(2, 5), 0));

	mockMvc.perform(get("/api/events")
		.param("page", "2")
		.param("size", "5"))
		.andExpect(status().isOk());

	ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
	verify(eventService).getAll(any(EventSpec.class), pageableCaptor.capture());

	assertEquals(2, pageableCaptor.getValue().getPageNumber());
	assertEquals(5, pageableCaptor.getValue().getPageSize());
    }

    @Test
    void getEventsWhenFiltersProvidedResolvesEventSpec() throws Exception {
	Page<EventViewResponse> page = new PageImpl<>(List.of(buildViewResponse()), PageRequest.of(0, 20), 1);

	when(eventService.getAll(any(EventSpec.class), any(Pageable.class))).thenReturn(page);

	mockMvc.perform(get("/api/events")
		.param("name", "Team")
		.param("status", EventStatus.PUBLISHED.name()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content[0].name").value("Team event"));

	ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
	verify(eventService).getAll(specCaptor.capture(), any(Pageable.class));

	assertNotNull(specCaptor.getValue());
    }

    @Test
    void getEventsWhenServiceThrowsUnexpectedExceptionReturnsInternalServerError() throws Exception {
	when(eventService.getAll(any(EventSpec.class), any(Pageable.class)))
		.thenThrow(new RuntimeException("Database unavailable"));

	mockMvc.perform(get("/api/events"))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    private EventViewResponse buildViewResponse() {
	return new EventViewResponse(1, "Team event", null, EventStatus.PUBLISHED,
		EventType.EXTERNAL, Location.CLUJ_NAPOCA);
    }
}
