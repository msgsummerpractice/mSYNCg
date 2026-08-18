package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import com.example.demo.model.EventType;
import com.example.demo.model.Location;
import com.example.demo.validator.ImageType;
import com.example.demo.validator.MaxFileSize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {

    @NotBlank(message = "Event name is required")
    private String eventName;

    @NotNull(message = "Event type is required")
    private EventType eventType;

    private Location eventLocation;

    @NotNull(message = "Event start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "Event end time is required")
    private LocalDateTime endTime;

    private Boolean foodProvided;

    @NotBlank(message = "Event description is required")
    private String description;

    @NotNull(message = "Event registration start time is required")
    private LocalDateTime registrationStartTime;

    @NotNull(message = "Event registration end time is required")
    private LocalDateTime registrationEndTime;

    @MaxFileSize(value = 5000000, message = "Poster file size exceeds the maximum limit of 5MB.")
    @ImageType(allowedTypes = { "image/jpeg", "image/png" }, message = "Invalid poster image type. Allowed types are JPEG and PNG.")
    private byte[] poster;
}
