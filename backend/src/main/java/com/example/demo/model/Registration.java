package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.Objects;

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
    private RegistrationStatus status;

    @Column(nullable = false, insertable = false)
    private Instant date;

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

    @Column(name = "driver_name", length = 50)
    private String driverName;

    @Column(name = "driver_phone", length = 20)
    private String driverPhone;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Registration that = (Registration) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(status, that.status) &&
                Objects.equals(date, that.date) &&
                foodPreference == that.foodPreference &&
                Objects.equals(accommodationDays, that.accommodationDays) &&
                Objects.equals(gdpr, that.gdpr) &&
                Objects.equals(photoConsent, that.photoConsent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, date, foodPreference, accommodationDays, gdpr, photoConsent);
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