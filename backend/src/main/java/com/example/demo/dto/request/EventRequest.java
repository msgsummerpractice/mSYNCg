package com.example.demo.dto.request;
 
import jakarta.validation.constraints.NotBlank;
 
import java.time.LocalDateTime;
 
import com.example.demo.model.EventType;
import com.example.demo.model.Location;
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
 
    @NotBlank(message = "Event type is required")
    private EventType eventType;
 
    private Location eventLocation;
 
    private LocalDateTime startTime;
 
    private LocalDateTime endTime;
 
    private boolean foodProvided;
 
    @MaxFileSize(value=5000000, message = "Poster file size exceeds the maximum limit of 5MB.")
    private byte[] poster;
}