package com.example.demo.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.demo.exceptions.ValidationException;
import com.example.demo.model.Event;
import com.example.demo.model.EventStatus;
import com.example.demo.model.EventType;
import com.example.demo.model.FoodPreference;
import com.example.demo.model.Location;
import com.example.demo.model.Registration;
import com.example.demo.model.RegistrationStatus;
import com.example.demo.model.User;
import com.example.demo.repository.RegistrationRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrationValidatorTests {

    @Mock
    private RegistrationRepository registrationRepository;

    private RegistrationValidator registrationValidator;

    @BeforeEach
    void setUp() {
        registrationValidator = new RegistrationValidator(registrationRepository);
    }

    @Test
    void validate_whenRegistrationMeetsAllRules_doesNotThrow() {
        Registration registration = createRegistration();

        assertDoesNotThrow(() -> registrationValidator.validate(registration, LocalDateTime.now()));
    }

    @Test
    void validate_whenUserIsAlreadyRegistered_rejectsEventId() {
        Registration registration = createRegistration();
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(1, 2, RegistrationStatus.REGISTERED))
                .thenReturn(true);

        ValidationException exception = assertThrows(
                ValidationException.class, () -> registrationValidator.validate(registration, LocalDateTime.now()));

        assertEquals("eventId", exception.getField());
    }

    @Test
    void validate_whenRegistrationHasClosed_rejectsServerTime() {
        Registration registration = createRegistration();
        registration.getEvent().setRegistrationEnd(LocalDateTime.now().minusSeconds(1));

        ValidationException exception = assertThrows(
                ValidationException.class, () -> registrationValidator.validate(registration, LocalDateTime.now()));

        assertEquals("date", exception.getField());
    }

    @Test
    void validate_whenPhotoConsentIsNotAcknowledged_rejectsPhotoConsent() {
        Registration registration = createRegistration();
        registration.setPhotoConsent(false);

        ValidationException exception = assertThrows(
                ValidationException.class, () -> registrationValidator.validate(registration, LocalDateTime.now()));

        assertEquals("photoConsent", exception.getField());
    }

    @Test
    void validate_whenFoodIsProvidedWithoutPreference_rejectsFoodPreference() {
        Registration registration = createRegistration();
        registration.getEvent().setFoodProvided(true);
        registration.setFoodPreference(null);

        ValidationException exception = assertThrows(
                ValidationException.class, () -> registrationValidator.validate(registration, LocalDateTime.now()));

        assertEquals("foodPreference", exception.getField());
    }

    @Test
    void validate_whenInternalTransportationHasOnlyDriverName_rejectsDriverDetails() {
        Registration registration = createRegistration();
        registration.getEvent().setType(EventType.INTERNAL);
        registration.setGdpr(true);
        registration.setDriverName("Jane Driver");

        ValidationException exception = assertThrows(
                ValidationException.class, () -> registrationValidator.validate(registration, LocalDateTime.now()));

        assertEquals("driverName", exception.getField());
    }

    @Test
    void validate_whenInternalAccommodationDaysAreNotPositive_rejectsAccommodationDays() {
        Registration registration = createRegistration();
        registration.getEvent().setType(EventType.INTERNAL);
        registration.setGdpr(true);
        registration.setAccommodationDays(0);

        ValidationException exception = assertThrows(
                ValidationException.class, () -> registrationValidator.validate(registration, LocalDateTime.now()));

        assertEquals("accommodationDays", exception.getField());
    }

    private Registration createRegistration() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setId(1);
        user.setLocation(Location.CLUJ_NAPOCA);

        Event event = new Event();
        event.setId(2);
        event.setLocation(Location.CLUJ_NAPOCA);
        event.setStatus(EventStatus.PUBLISHED);
        event.setType(EventType.EXTERNAL);
        event.setFoodProvided(false);
        event.setRegistrationStart(now.minusHours(1));
        event.setRegistrationEnd(now.plusHours(1));

        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setDate(now);
        registration.setGdpr(false);
        registration.setPhotoConsent(true);
        registration.setFoodPreference(FoodPreference.NONE);
        return registration;
    }
}