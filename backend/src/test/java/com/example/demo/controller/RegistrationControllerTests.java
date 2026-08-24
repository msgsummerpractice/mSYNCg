package com.example.demo.controller;

import com.example.demo.dto.request.RegistrationRequest;
import com.example.demo.dto.response.RegistrationDetailsResponse;
import com.example.demo.dto.response.RegistrationResponse;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.RegistrationClosedException;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    void getRegistration_whenRegistrationExists_returnsOkWithDetails() throws Exception {
        when(registrationService.getRegistration(2, 1)).thenReturn(buildDetailsResponse());

        mockMvc.perform(get("/api/registrations")
                .param("eventId", "2")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-09-01T10:00:00"))
                .andExpect(jsonPath("$.foodPreference").value(FoodPreference.VEGAN.name()))
                .andExpect(jsonPath("$.accommodationDays").value(3))
                .andExpect(jsonPath("$.gdpr").value(true))
                .andExpect(jsonPath("$.photoConsent").value(true))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.eventId").value(2))
                .andExpect(jsonPath("$.driverName").value("Jane Driver"))
                .andExpect(jsonPath("$.driverPhone").value("0700000000"))
                .andExpect(jsonPath("$.editable").value(true));

        verify(registrationService).getRegistration(2, 1);
    }

    @Test
    void getRegistration_whenRegistrationDoesNotExist_returnsNotFound() throws Exception {
        when(registrationService.getRegistration(2, 1))
                .thenThrow(new NotFoundException("Registration for user 1 and event 2 not found"));

        mockMvc.perform(get("/api/registrations")
                .param("eventId", "2")
                .param("userId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Registration for user 1 and event 2 not found"));
    }

    @Test
    void getRegistration_whenParamsAreMissing_returnsInternalServerError() throws Exception {
        mockMvc.perform(get("/api/registrations"))
                .andExpect(status().isInternalServerError());

        verifyNoInteractions(registrationService);
    }

    @Test
    void getRegistration_whenParamIsNotANumber_returnsInternalServerError() throws Exception {
        mockMvc.perform(get("/api/registrations")
                .param("eventId", "abc")
                .param("userId", "1"))
                .andExpect(status().isInternalServerError());

        verifyNoInteractions(registrationService);
    }

    @Test
    void updateRegistration_whenRequestIsValid_returnsOkWithUpdatedDetails() throws Exception {
        ArgumentCaptor<RegistrationRequest> requestCaptor = ArgumentCaptor.forClass(RegistrationRequest.class);

        when(registrationService.updateRegistration(requestCaptor.capture()))
                .thenReturn(buildDetailsResponse());

        mockMvc.perform(put("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.eventId").value(2))
                .andExpect(jsonPath("$.editable").value(true));

        RegistrationRequest forwardedRequest = requestCaptor.getValue();
        assertEquals(2, forwardedRequest.getEventId());
        assertEquals(1, forwardedRequest.getUserId());
        assertEquals(FoodPreference.NONE, forwardedRequest.getFoodPreference());
        assertEquals(2, forwardedRequest.getAccommodationDays());
        assertEquals("Jane Driver", forwardedRequest.getDriverName());
    }

    @Test
    void updateRegistration_whenRequiredFieldsAreMissing_returnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(3));

        verify(registrationService, never()).updateRegistration(any(RegistrationRequest.class));
    }

    @Test
    void updateRegistration_whenRegistrationPeriodHasEnded_returnsConflict() throws Exception {
        when(registrationService.updateRegistration(any(RegistrationRequest.class)))
                .thenThrow(new RegistrationClosedException(
                        "Registration period has ended. The registration can only be deleted."));

        mockMvc.perform(put("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("Registration period has ended. The registration can only be deleted."));
    }

    @Test
    void updateRegistration_whenRegistrationDoesNotExist_returnsNotFound() throws Exception {
        when(registrationService.updateRegistration(any(RegistrationRequest.class)))
                .thenThrow(new NotFoundException("Registration for user 1 and event 2 not found"));

        mockMvc.perform(put("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void deleteRegistration_whenRegistrationExists_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lookupJson(2, 1)))
                .andExpect(status().isNoContent());

        verify(registrationService).deleteRegistration(2, 1);
    }

    @Test
    void deleteRegistration_whenRequiredFieldsAreMissing_returnsBadRequest() throws Exception {
        mockMvc.perform(delete("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.fieldErrors.length()").value(2));

        verify(registrationService, never()).deleteRegistration(anyInt(), anyInt());
    }

    @Test
    void deleteRegistration_whenRegistrationDoesNotExist_returnsNotFound() throws Exception {
        doThrow(new NotFoundException("Registration for user 1 and event 2 not found"))
                .when(registrationService).deleteRegistration(2, 1);

        mockMvc.perform(delete("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lookupJson(2, 1)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Registration for user 1 and event 2 not found"));
    }

    @Test
    void deleteRegistration_whenServiceThrowsUnexpectedException_returnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("Database unavailable"))
                .when(registrationService).deleteRegistration(2, 1);

        mockMvc.perform(delete("/api/registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lookupJson(2, 1)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    private String lookupJson(Integer eventId, Integer userId) {
        return "{\"eventId\": %d, \"userId\": %d}".formatted(eventId, userId);
    }

    private RegistrationDetailsResponse buildDetailsResponse() {
        return new RegistrationDetailsResponse(
                LocalDateTime.of(2026, 9, 1, 10, 0),
                FoodPreference.VEGAN,
                3,
                true,
                true,
                1,
                2,
                "Jane Driver",
                "0700000000",
                true);
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