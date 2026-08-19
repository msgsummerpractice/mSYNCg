package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import com.example.demo.model.EventType;
import com.example.demo.model.Location;
import com.example.demo.validator.MaxFileSize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventRequest {

    @NotBlank(message = "Event name is required")
    private String name;

    @NotNull(message = "Event type is required")
    private EventType type;

    @NotNull(message = "Event location is required")
    private Location location;

    @NotNull(message = "Event start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "Event end time is required")
    private LocalDateTime endTime;

    private boolean foodProvided;

    @NotNull(message = "Event description is required")
    private String description;

    @NotNull(message = "Event registration start time is required")
    private LocalDateTime registrationStart;

    @NotNull(message = "Event registration end time is required")
    private LocalDateTime registrationEnd;

    @MaxFileSize(value = 5000000, message = "Poster file size exceeds the maximum limit of 5MB.")
    private String imageBase64;
}