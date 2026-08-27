package com.example.demo.dto.response;

import java.time.Instant;

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
    private Instant startTime;
    private Instant endTime;
    private Boolean foodProvided;
    private Instant registrationStart;
    private Instant registrationEnd;
    private String description;
    private String qrCode;
    private String code;
}
