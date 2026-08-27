package com.example.demo.dto.response;

import java.time.Instant;
import com.example.demo.model.EventType;
import com.example.demo.model.Location;
import com.example.demo.model.EventStatus;
import com.example.demo.model.EventParticipationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EventViewResponse {
    private Integer id;
    private String name;
    private Instant startTime;
    private Instant endTime;
    private EventStatus status;
    private EventType type;
    private Location location;
    private EventParticipationStatus participationStatus;
}
