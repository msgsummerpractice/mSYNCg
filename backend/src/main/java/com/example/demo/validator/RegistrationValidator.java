package com.example.demo.validator;

import com.example.demo.exceptions.ValidationException;
import com.example.demo.model.Event;
import com.example.demo.model.EventStatus;
import com.example.demo.model.EventType;
import com.example.demo.model.Location;
import com.example.demo.model.Registration;
import com.example.demo.repository.RegistrationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationValidator {

    private final RegistrationRepository registrationRepository;

    public void validate(Registration registration, LocalDateTime registrationTime) {
        Event event = registration.getEvent();

        validateDuplicateRegistration(registration);
        validateLocation(registration);
        validateEventStatus(event);
        validateRegistrationPeriod(event, registrationTime);
        validateGdprConsent(registration);
        validatePhotoConsent(registration);
        validateFoodPreference(registration);
        validateInternalEventDetails(registration);
    }

    private void validateDuplicateRegistration(Registration registration) {
        if (registrationRepository.existsByUserIdAndEventId(
                registration.getUser().getId(), registration.getEvent().getId())) {
            throw new ValidationException("eventId", "User is already registered for this event.");
        }
    }

    private void validateLocation(Registration registration) {
        Location eventLocation = registration.getEvent().getLocation();
        Location userLocation = registration.getUser().getLocation();

        if (eventLocation != Location.ALL && eventLocation != userLocation) {
            throw new ValidationException("eventId", "User is not eligible for this event.");
        }
    }

    private void validateEventStatus(Event event) {
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new ValidationException("eventId", "Event is not published.");
        }
    }

    private void validateRegistrationPeriod(Event event, LocalDateTime registrationTime) {
        if (registrationTime.isBefore(event.getRegistrationStart())
                || registrationTime.isAfter(event.getRegistrationEnd())) {
            throw new ValidationException("date", "Registration is not open for this event.");
        }
    }

    private void validateGdprConsent(Registration registration) {
        if (registration.getEvent().getType() != EventType.EXTERNAL
                && !Boolean.TRUE.equals(registration.getGdpr())) {
            throw new ValidationException("gdpr", "GDPR consent is required.");
        }
    }

    private void validatePhotoConsent(Registration registration) {
        if (!Boolean.TRUE.equals(registration.getPhotoConsent())) {
            throw new ValidationException("photoConsent", "Photo consent must be acknowledged.");
        }
    }

    private void validateFoodPreference(Registration registration) {
        if (Boolean.TRUE.equals(registration.getEvent().getFoodProvided())
                && registration.getFoodPreference() == null) {
            throw new ValidationException("foodPreference", "Food preference is required for this event.");
        }
    }

    private void validateInternalEventDetails(Registration registration) {
        if (registration.getEvent().getType() != EventType.INTERNAL) {
            return;
        }

        boolean hasDriverName = registration.getDriverName() != null && !registration.getDriverName().isBlank();
        boolean hasDriverPhone = registration.getDriverPhone() != null && !registration.getDriverPhone().isBlank();

        if (hasDriverName != hasDriverPhone) {
            throw new ValidationException("driverName", "Driver name and phone must be provided together.");
        }

        if (registration.getAccommodationDays() != null && registration.getAccommodationDays() <= 0) {
            throw new ValidationException("accommodationDays", "Accommodation days must be greater than zero.");
        }
    }
}