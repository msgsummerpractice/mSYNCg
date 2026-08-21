package com.example.demo.controller;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.dto.response.EventViewResponse;
import com.example.demo.exceptions.EventCannotBeCompletedException;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.filtering.events.EventSpec;
import com.example.demo.model.EventStatus;
import com.example.demo.model.EventType;
import com.example.demo.model.Location;
import com.example.demo.service.EventService;
import com.example.demo.service.notification.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        @Mock
        private EmailService emailService;

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
                                .andExpect(jsonPath("$.fieldErrors.length()").value(9));

                verify(eventService, never()).create(
                                org.mockito.ArgumentMatchers.any(EventRequest.class),
                                org.mockito.ArgumentMatchers.anyString());
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
                                .andExpect(jsonPath("$.fieldErrors[0].reason").value("Event name cannot be blank"));

                ArgumentCaptor<EventRequest> requestCaptor = ArgumentCaptor.forClass(EventRequest.class);
                verify(eventService, never()).create(requestCaptor.capture(), isNull(String.class));
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
                assertNotNull(specCaptor.getValue());
                assertNotNull(pageableCaptor.getValue());
        }

        @Test
        void getEvents_whenPaginationParamsProvided_forwardsPageableToService() throws Exception {
                ArgumentCaptor<EventSpec> specCaptor = ArgumentCaptor.forClass(EventSpec.class);
                ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
                when(eventService.getAll(specCaptor.capture(), pageableCaptor.capture()))
                                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(2, 5), 0));

                mockMvc.perform(get("/api/events").param("page", "2").param("size", "5"))
                                .andExpect(status().isOk());
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

        @Test
        void getEvents_whenFiltersProvided_resolvesEventSpec() throws Exception {
                Page<EventViewResponse> page = new PageImpl<>(List.of(buildViewResponse()), PageRequest.of(0, 20), 1);
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
                                  "registrationEnd": "2026-08-31T18:00:00",
                                  "image": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA"
                                }
                                """;
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
                                  "registrationEnd": "%s",
                                  "image": "iVBORw0KGgoAAAANSUhEUgAAAAUA"
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
        }

        @Test
        void updateEvent_whenValidRequest_returnsUpdatedEvent() throws Exception {
                Integer eventId = 1;
                EventViewResponse updatedResponse = buildViewResponse();
                EventRequest eventRequest = createEventRequest("Updated Team event");
                when(eventService.updateEvent(eventId, eventRequest)).thenReturn(updatedResponse);
                mockMvc.perform(put("/api/events/{eventId}", eventId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(eventRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Team event"))
                                .andExpect(jsonPath("$.status").value(EventStatus.PUBLISHED.name()));

                verify(eventService).updateEvent(eventId, eventRequest);
        }

        @Test
        void publishEvent_whenEventExists_returnsOk() throws Exception {
                when(eventService.publishEvent(1)).thenReturn(new EventResponse(1L, EventStatus.PUBLISHED.name()));

                mockMvc.perform(patch("/api/events/1/publish"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.status").value(EventStatus.PUBLISHED.name()));

                verify(eventService).publishEvent(1);
        }

        @Test
        void publishEvent_whenEventDoesNotExist_returnsNotFound() throws Exception {
                when(eventService.publishEvent(99)).thenThrow(new NotFoundException("Event", 99));

                mockMvc.perform(patch("/api/events/99/publish"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value("Not Found"))
                                .andExpect(jsonPath("$.message").value("Event with id 99 not found"));
        }

        @Test
        void publishEvent_whenServiceThrowsUnexpectedException_returnsInternalServerError() throws Exception {
                when(eventService.publishEvent(1)).thenThrow(new RuntimeException("Database unavailable"));

                mockMvc.perform(patch("/api/events/1/publish"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value("Internal Server Error"));
        }

        @Test
        void updateEvent_whenEventNotFound_returnsInternalServerError() throws Exception {
                Integer eventId = 999;
                EventRequest eventRequest = createEventRequest("Nonexistent event");

                when(eventService.updateEvent(eventId, eventRequest))
                                .thenThrow(new RuntimeException("Event not found with id: " + eventId));

                mockMvc.perform(put("/api/events/{eventId}", eventId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(eventRequest)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value("Internal Server Error"));

                verify(eventService).updateEvent(eventId, eventRequest);
        }

        @Test
        void updateEvent_whenImageBase64Provided_updatesAndReturnsEvent() throws Exception {
                Integer eventId = 1;
                String imageBase64 = "aGVsbG8gd29ybGQ=";
                EventViewResponse updatedResponse = buildViewResponse();
                EventRequest eventRequest = createEventRequest("Event with image", imageBase64);

                when(eventService.updateEvent(eventId, eventRequest)).thenReturn(updatedResponse);

                mockMvc.perform(put("/api/events/{eventId}", eventId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(eventRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Team event"));

                verify(eventService).updateEvent(eventId, eventRequest);
        }

        @Test
        void updateEvent_whenNoImageProvided_updatesWithoutImage() throws Exception {
                Integer eventId = 1;
                EventViewResponse updatedResponse = buildViewResponse();
                EventRequest eventRequest = createEventRequest("Event without image", null);

                when(eventService.updateEvent(eventId, eventRequest)).thenReturn(updatedResponse);

                mockMvc.perform(put("/api/events/{eventId}", eventId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(eventRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));

                verify(eventService).updateEvent(eventId, eventRequest);
        }

        private EventViewResponse buildViewResponse() {
                return new EventViewResponse(1, "Team event", null, null, EventStatus.PUBLISHED,
                                EventType.EXTERNAL, Location.CLUJ_NAPOCA);
        }

        private EventRequest createEventRequest(String name) {
                return createEventRequest(name, null);
        }

        private EventRequest createEventRequest(String name, String imageBase64) {
                EventRequest request = new EventRequest();
                request.setName(name);
                request.setImage(imageBase64);
                return request;
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

        void completeEvent_whenEventCannotBeCompleted_returnsBadRequest() throws Exception {
                when(eventService.completeEvent(1))
                                .thenThrow(new EventCannotBeCompletedException(
                                                "Event cannot be completed before its end time."));

                mockMvc.perform(patch("/api/events/1/complete"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Bad Request"))
                                .andExpect(jsonPath("$.message")
                                                .value("Event cannot be completed before its end time."));
        }
}
