package com.example.demo.controller;

import com.example.demo.dto.request.CheckInRequest;
import com.example.demo.exceptions.AlreadyCheckedInException;
import com.example.demo.exceptions.EventAlreadyCompletedException;
import com.example.demo.exceptions.EventCheckInExpiredException;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.exceptions.InvalidCheckInException;
import com.example.demo.exceptions.UserNotRegisteredException;
import com.example.demo.service.AttendanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CheckInControllerTests {

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private CheckInController checkInController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(checkInController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void checkIn_whenRequestIsValid_returnsOk() throws Exception {
        doNothing().when(attendanceService).checkIn(anyString());

        mockMvc.perform(post("/api/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isOk());

        verify(attendanceService).checkIn("Event:Test Event|ID:1");
    }

    @Test
    void checkIn_whenRequestIsValidWithSixDigitCode_returnsOk() throws Exception {
        doNothing().when(attendanceService).checkIn(anyString());

        mockMvc.perform(post("/api/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson("123456")))
                .andExpect(status().isOk());

        verify(attendanceService).checkIn("123456");
    }

    @Test
    void checkIn_whenValueIsMissing_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));

        verify(attendanceService, never()).checkIn(anyString());
    }

    @Test
    void checkIn_whenValueIsBlank_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson("")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));

        verify(attendanceService, never()).checkIn(anyString());
    }

    @Test
    void checkIn_whenInvalidCheckInException_returnsBadRequest() throws Exception {
        doThrow(new InvalidCheckInException()).when(attendanceService).checkIn(anyString());

        mockMvc.perform(post("/api/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void checkIn_whenUserNotRegisteredException_returnsBadRequest() throws Exception {
        doThrow(new UserNotRegisteredException()).when(attendanceService).checkIn(anyString());

        mockMvc.perform(post("/api/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void checkIn_whenEventAlreadyCompletedException_returnsBadRequest() throws Exception {
        doThrow(new EventAlreadyCompletedException()).when(attendanceService).checkIn(anyString());

        mockMvc.perform(post("/api/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void checkIn_whenEventCheckInExpiredException_returnsBadRequest() throws Exception {
        doThrow(new EventCheckInExpiredException()).when(attendanceService).checkIn(anyString());

        mockMvc.perform(post("/api/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void checkIn_whenAlreadyCheckedInException_returnsBadRequest() throws Exception {
        doThrow(new AlreadyCheckedInException()).when(attendanceService).checkIn(anyString());

        mockMvc.perform(post("/api/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void checkIn_whenUnexpectedException_returnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("Database unavailable")).when(attendanceService).checkIn(anyString());

        mockMvc.perform(post("/api/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestJson()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Database unavailable"));
    }

    private String validRequestJson() {
        return requestJson("Event:Test Event|ID:1");
    }

    private String requestJson(String value) {
        return """
                {
                  "value": "%s"
                }
                """.formatted(value);
    }
}
