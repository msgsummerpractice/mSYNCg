package com.example.demo.dto.response;

import java.time.Instant;

import com.example.demo.model.FoodPreference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationDetailsResponse {

    private Instant date;

    private FoodPreference foodPreference;

    private Integer accommodationDays;

    private Boolean gdpr;

    private Boolean photoConsent;

    private Integer userId;

    private Integer eventId;

    private String driverName;

    private String driverPhone;

    private Boolean editable;
}
