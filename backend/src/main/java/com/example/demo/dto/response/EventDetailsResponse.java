package com.example.demo.dto.response;

import java.time.LocalDateTime;

import com.example.demo.model.EventStatus;
import com.example.demo.model.EventType;
import com.example.demo.model.Location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventDetailsResponse {
    private Integer id;
    private String name;
    private EventStatus status;
    private EventType type;
    private Location location;
    private String image;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean foodProvided;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private String description;
    private String qrCode;
    private String code;
}
