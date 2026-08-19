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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventRequest {

    @NotNull(message = "Event name is required")
    @NotBlank(message = "Event name cannot be blank")
    private String name;

    @NotNull(message = "Event type is required")
    @NotBlank(message = "Event type cannot be blank")
    private EventType type;

    @NotNull(message = "Event location is required")
    @NotBlank(message = "Event location cannot be blank")
    private Location location;

    @NotNull(message = "Event start time is required")
    @NotBlank(message = "Event start time cannot be blank")
    private LocalDateTime startTime;

    @NotNull(message = "Event end time is required")
    @NotBlank(message = "Event end time cannot be blank")
    private LocalDateTime endTime;

    private Boolean foodProvided;

    @NotNull(message = "Event description is required")
    @NotBlank(message = "Event description cannot be blank")
    private String description;

    @NotNull(message = "Event registration start time is required")
    @NotBlank(message = "Event registration start time cannot be blank")
    private LocalDateTime registrationStart;

    @NotNull(message = "Event registration end time is required")
    @NotBlank(message = "Event registration end time cannot be blank")
    private LocalDateTime registrationEnd;

    @MaxFileSize(value = 5000000, message = "Poster file size exceeds the maximum limit of 5MB.")
    @ImageType(allowedTypes = { "image/jpeg",
            "image/png" }, message = "Invalid poster image type. Allowed types are JPEG and PNG.")
    @NotBlank(message = "Event poster is required")
    private String image;
}
