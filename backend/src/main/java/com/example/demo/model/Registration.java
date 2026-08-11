package  main.java.com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String status;

    @Column(nullable = false, insertable = false)
    private LocalDateTime date;

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
}