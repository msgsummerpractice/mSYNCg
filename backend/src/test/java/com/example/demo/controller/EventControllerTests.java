package com.example.demo.controller;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.EventCannotBeCompletedException;
import com.example.demo.dto.request.EventRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventControllerTests {

        @Mock
        private EventService eventService;

        @InjectMocks
        private EventController eventController;

        private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                "organizer@example.com",
                                null);

                mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                                .setCustomArgumentResolvers(
                                                new SpecificationArgumentResolver(),
                                                new PageableHandlerMethodArgumentResolver())
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .defaultRequest(get("/")
                                                .principal(authentication))
                                .build();
        }

        @Test
        void createEvent_WhenRequiredFieldsMissing_ReturnsBadRequest() throws Exception {
                mockMvc.perform(post("/api/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error").value("Validation Error"))
                                .andExpect(jsonPath("$.message").value("Request validation failed"))
                                .andExpect(jsonPath("$.fieldErrors.length()").value(8));

                ArgumentCaptor<EventRequest> requestCaptor = ArgumentCaptor.forClass(EventRequest.class);
                verify(eventService, never()).create(requestCaptor.capture(), isNull(String.class));
        }

        @Test
        void createEvent_WhenNameIsNull_ReturnsBadRequest() throws Exception {
                mockMvc.perform(post("/api/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson(null, "LOCAL", "Cluj-Napoca", "2026-09-01T09:00:00",
                                                "2026-09-01T12:00:00", true,
                                                "Internal event for the engineering team", "2026-08-15T09:00:00",
                                                "2026-08-31T18:00:00")))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"))
                                .andExpect(jsonPath("$.fieldErrors[0].reason").value("Event name is required"));

                ArgumentCaptor<EventRequest> requestCaptor = ArgumentCaptor.forClass(EventRequest.class);
                verify(eventService, never()).create(requestCaptor.capture(), isNull(String.class));
        }

        @Test
        void getEvents_whenNoResults_returnsEmptyPage() throws Exception {
        ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(eventService.getAll(specCaptor.capture(), pageableCaptor.capture()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));
        }

        @Test
        void createEvent_WhenServiceThrowsUnexpectedException_ReturnsInternalServerError() throws Exception {
        ArgumentCaptor<EventRequest> requestCaptor = ArgumentCaptor.forClass(EventRequest.class);

        when(eventService.create(requestCaptor.capture(), eq("organizer@example.com")))
                .thenThrow(new RuntimeException("Database unavailable"));

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Database unavailable"));
        }

        private String validRequestJson() {
                return """
                                {
                                  "name": "Engineering Meetup",
                                  "type": "LOCAL",
                                  "location": "Cluj-Napoca",
                                  "startTime": "2026-09-01T09:00:00",
                                  "endTime": "2026-09-01T12:00:00",
                                  "foodProvided": true,
                                  "description": "Internal event for the engineering team",
                                  "registrationStart": "2026-08-15T09:00:00",
                                  "registrationEnd": "2026-08-31T18:00:00"
                                }
                                """;
        }

    @Test
	void getEvents_whenPaginationParamsProvided_forwardsPageableToService() throws Exception {
		ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		when(eventService.getAll(specCaptor.capture(), pageableCaptor.capture()))
				.thenReturn(new PageImpl<>(List.of(), PageRequest.of(2, 5), 0));
        }

        private String requestJson(String name, String type, String location, String startTime,
                        String endTime, boolean foodProvided, String description, String registrationStart,
                        String registrationEnd) {
                String nameJson = name == null
                                ? "null"
                                : "\"" + name + "\"";
                return """
                                {
                                  "name": %s,
                                  "type": "%s",
                                  "location": "%s",
                                  "startTime": "%s",
                                  "endTime": "%s",
                                  "foodProvided": %s,
                                  "description": "%s",
                                  "registrationStart": "%s",
                                  "registrationEnd": "%s"
                                }
                                """.formatted(nameJson, type, location, startTime, endTime, foodProvided,
                                description,
                                registrationStart, registrationEnd);
        }

        @Test
        void getEvents_WhenNoFilters_ReturnsPageOfEvents() throws Exception {
                Page<EventViewResponse> page = new PageImpl<>(List.of(buildViewResponse()), PageRequest.of(0, 20), 1);

                ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
                when(eventService.getAll(specCaptor.capture(), pageableCaptor.capture())).thenReturn(page);
        }

        @Test
        void getEvents_whenFiltersProvided_resolvesEventSpec() throws Exception {
                Page<EventViewResponse> page = new PageImpl<>(List.of(buildViewResponse()), PageRequest.of(0, 20), 1);
                ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
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
        void getEvents_WhenNoResults_ReturnsEmptyPage() throws Exception {
                ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
                when(eventService.getAll(specCaptor.capture(), pageableCaptor.capture()))
                                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

                mockMvc.perform(get("/api/events").param("name", "Nobody"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isEmpty())
                                .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        void getEvents_whenServiceThrowsUnexpectedException_returns_internalServerError() throws Exception {
                ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
                when(eventService.getAll(specCaptor.capture(), pageableCaptor.capture()))
                                .thenThrow(new RuntimeException("Database unavailable"));

                assertNotNull(specCaptor.getValue());
                assertNotNull(pageableCaptor.getValue());
        }

        @Test
        void getEvents_WhenPaginationParamsProvided_ForwardsPageableToService() throws Exception {
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
        void getEvents_WhenFiltersProvided_ResolvesEventSpec() throws Exception {
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
        void getEvents_WhenServiceThrowsUnexpectedException_ReturnsInternalServerError() throws Exception {
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

        @Test
        void createEvent_IsRestrictedToAllowedRoles() throws Exception {
                PreAuthorize pre = EventController.class
                                .getMethod("createEvent", com.example.demo.dto.request.EventRequest.class,
                                                org.springframework.security.core.Authentication.class)
                                .getAnnotation(PreAuthorize.class);

                String expr = pre == null ? null : pre.value();

                assertNotNull(expr);
                assertTrue(expr.contains("MARKETING_ORGANIZER"));
                assertFalse(expr.contains("PARTICIPANT"));
                assertFalse(expr.contains("ADMIN"));
                assertFalse(expr.contains("HR_USER"));
        }

        private EventViewResponse buildViewResponse() {
                return new EventViewResponse(1, "Team event", null, EventStatus.PUBLISHED,
                                EventType.EXTERNAL, Location.CLUJ_NAPOCA);
        }
}