package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import com.example.demo.model.FoodPreference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationRequest {

    @NotNull(message = "Date is required")
    private LocalDateTime date;

    private FoodPreference foodPreference;

    private Integer accommodationDays;

    private Boolean gdpr;

    private Boolean photoConsent;

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Event ID is required")
    private Integer eventId;

    private String driverName;

    private String driverPhone;
}
