package com.example.demo.controller;

import com.example.demo.dto.request.RegistrationRequest;
import com.example.demo.dto.response.RegistrationResponse;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.exceptions.ValidationException;
import com.example.demo.model.FoodPreference;
import com.example.demo.service.RegistrationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RegistrationControllerTests {

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegistrationController registrationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createRegistration_whenRequestIsValid_returnsOkWithResponse() throws Exception {
        RegistrationResponse response = new RegistrationResponse(1, 2);
        ArgumentCaptor<RegistrationRequest> requestCaptor = ArgumentCaptor.forClass(RegistrationRequest.class);

        when(registrationService.createRegistration(requestCaptor.capture())).thenReturn(response);

        mockMvc.perform(post("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.eventId").value(2));

        verify(registrationService).createRegistration(requestCaptor.capture());
    }

    @Test
    void createRegistration_whenRequiredFieldsAreMissing_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(3));

        verify(registrationService, never()).createRegistration(any(RegistrationRequest.class));
    }

    @Test
    void createRegistration_whenServiceThrowsValidationException_returnsBadRequest() throws Exception {
        when(registrationService.createRegistration(any(RegistrationRequest.class)))
                .thenThrow(new ValidationException("eventId", "User is already registered for this event."));

        mockMvc.perform(post("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("eventId"))
                .andExpect(jsonPath("$.fieldErrors[0].reason").value("User is already registered for this event."));
    }

    @Test
    void createRegistration_whenServiceThrowsUnexpectedException_returnsInternalServerError() throws Exception {
        when(registrationService.createRegistration(any(RegistrationRequest.class)))
                .thenThrow(new RuntimeException("Database unavailable"));

        mockMvc.perform(post("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Database unavailable"));
    }

    private String validRequestJson() {
        return requestJson("2026-09-01T10:00:00", FoodPreference.NONE.name(), 2, true, true, 1, 2,
                "Jane Driver", "0700000000");
    }

    private String requestJson(String date, String foodPreference, Integer accommodationDays,
            Boolean gdpr, Boolean photoConsent, Integer userId, Integer eventId,
            String driverName, String driverPhone) {
        return """
                {
                  "date": "%s",
                  "foodPreference": "%s",
                  "accommodationDays": %d,
                  "gdpr": %s,
                  "photoConsent": %s,
                  "userId": %d,
                  "eventId": %d,
                  "driverName": "%s",
                  "driverPhone": "%s"
                }
                """.formatted(
                date,
                foodPreference,
                accommodationDays,
                gdpr,
                photoConsent,
                userId,
                eventId,
                driverName,
                driverPhone);
    }
}