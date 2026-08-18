package com.example.demo.controller;

import com.example.demo.dto.request.EventRequest;
import com.example.demo.dto.response.EventResponse;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createEventWhenRequestIsValidReturnsOkAndDraftStatus() throws Exception {
        EventResponse serviceResponse = new EventResponse(7L, null);
        when(eventService.create(any(EventRequest.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.status").value("Draft"));

        ArgumentCaptor<EventRequest> requestCaptor = ArgumentCaptor.forClass(EventRequest.class);
        verify(eventService).create(requestCaptor.capture());
        assertNotNull(requestCaptor.getValue());
        assertEquals("Engineering Meetup", requestCaptor.getValue().getEventName());
    }

    @Test
    void createEventWhenRequiredFieldsMissingReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(7));

        verify(eventService, never()).create(any(EventRequest.class));
    }

    @Test
    void createEventWhenNameIsBlankReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson("", "LOCAL", "Cluj-Napoca", "2026-09-01T09:00:00", "2026-09-01T12:00:00", true,
                        "Internal event for the engineering team", "2026-08-15T09:00:00", "2026-08-31T18:00:00")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("eventName"));

        verify(eventService, never()).create(any(EventRequest.class));
    }

    @Test
    void createEventWhenServiceThrowsUnexpectedExceptionReturnsInternalServerError() throws Exception {
        when(eventService.create(any(EventRequest.class))).thenThrow(new RuntimeException("Database unavailable"));

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
                  "eventName": "Engineering Meetup",
                  "eventType": "LOCAL",
                  "eventLocation": "Cluj-Napoca",
                  "startTime": "2026-09-01T09:00:00",
                  "endTime": "2026-09-01T12:00:00",
                  "foodProvided": true,
                  "description": "Internal event for the engineering team",
                  "registrationStartTime": "2026-08-15T09:00:00",
                  "registrationEndTime": "2026-08-31T18:00:00"
                }
                """;
    }

    private String requestJson(String eventName, String eventType, String eventLocation, String startTime,
            String endTime, boolean foodProvided, String description, String registrationStartTime,
            String registrationEndTime) {
        return """
                {
                  "eventName": "%s",
                  "eventType": "%s",
                  "eventLocation": "%s",
                  "startTime": "%s",
                  "endTime": "%s",
                  "foodProvided": %s,
                  "description": "%s",
                  "registrationStartTime": "%s",
                  "registrationEndTime": "%s"
                }
                """.formatted(eventName, eventType, eventLocation, startTime, endTime, foodProvided, description,
                registrationStartTime, registrationEndTime);
    }
}
