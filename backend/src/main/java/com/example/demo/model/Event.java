package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private Location location;

    @Column(columnDefinition = "bytea")
    private byte[] image;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "food_provided", nullable = false)
    private Boolean foodProvided;

    @Column(name = "registration_start", nullable = false)
    private LocalDateTime registrationStart;

    @Column(name = "registration_end", nullable = false)
    private LocalDateTime registrationEnd;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User createdBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return java.util.Objects.equals(id, event.id) && 
               java.util.Objects.equals(name, event.name) && 
               status == event.status && 
               type == event.type && 
               location == event.location && 
               java.util.Arrays.equals(image, event.image) && 
               java.util.Objects.equals(startTime, event.startTime) && 
               java.util.Objects.equals(endTime, event.endTime) && 
               java.util.Objects.equals(foodProvided, event.foodProvided) && 
               java.util.Objects.equals(registrationStart, event.registrationStart) && 
               java.util.Objects.equals(registrationEnd, event.registrationEnd) && 
               java.util.Objects.equals(description, event.description) && 
               java.util.Objects.equals(createdAt, event.createdAt);
    }

    @Override
    public int hashCode() {
        int result = java.util.Objects.hash(id, name, status, type, location, startTime, endTime, foodProvided, registrationStart, registrationEnd, description, createdAt);
        result = 31 * result + java.util.Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", location=" + location +
                ", image=" + (image != null ? "[PREZENT]" : "[LIPSA]") +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", foodProvided=" + foodProvided +
                ", registrationStart=" + registrationStart +
                ", registrationEnd=" + registrationEnd +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}