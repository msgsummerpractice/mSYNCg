package com.example.demo.dto.response;

import java.time.LocalDateTime;
import com.example.demo.model.EventType;
import com.example.demo.model.Location;
import com.example.demo.model.EventStatus;

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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EventStatus status;
    private EventType type;
    private Location location;
}
