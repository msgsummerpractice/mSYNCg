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
@Table(name = "registrations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventStatus status;

    @Column(nullable = false, insertable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_preference", length = 50)
    private FoodPreference foodPreference;

    @Column(name = "accommodation_days")
    private Integer accommodationDays;

    @Column(nullable = false)
    private Boolean gdpr;

    @Column(name = "photo_consent", nullable = false)
    private Boolean photoConsent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Driver driver;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return java.util.Objects.equals(id, that.id) && 
               java.util.Objects.equals(status, that.status) && 
               java.util.Objects.equals(date, that.date) && 
               foodPreference == that.foodPreference && 
               java.util.Objects.equals(accommodationDays, that.accommodationDays) && 
               java.util.Objects.equals(gdpr, that.gdpr) && 
               java.util.Objects.equals(photoConsent, that.photoConsent);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, status, date, foodPreference, accommodationDays, gdpr, photoConsent);
    }

    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", date=" + date +
                ", foodPreference=" + foodPreference +
                ", accommodationDays=" + accommodationDays +
                ", gdpr=" + gdpr +
                ", photoConsent=" + photoConsent +
                '}';
    }
}