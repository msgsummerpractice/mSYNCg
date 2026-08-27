package com.example.demo.service;

import com.example.demo.dto.request.RegistrationRequest;
import com.example.demo.dto.response.RegistrationDetailsResponse;
import com.example.demo.dto.response.RegistrationResponse;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.RegistrationClosedException;
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
import com.example.demo.validator.RegistrationValidator;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTests {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RegistrationValidator registrationValidator;

    @Mock
    private UserService userService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void createRegistration_whenRequestIsValid_mapsAndSavesRegistration() {
        RegistrationRequest request = buildRequest();
        Registration mappedRegistration = new Registration();
        RegistrationResponse mappedResponse = new RegistrationResponse(1, 2);
        User user = createUser(1);
        Event event = createEvent(2);
        ArgumentCaptor<Registration> registrationCaptor = ArgumentCaptor.forClass(Registration.class);

        when(modelMapper.map(request, Registration.class)).thenReturn(mappedRegistration);
        when(userService.findById(1)).thenReturn(user);
        when(eventService.findEventById(2)).thenReturn(event);
        when(registrationRepository.save(registrationCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(mappedRegistration, RegistrationResponse.class)).thenReturn(mappedResponse);

        RegistrationResponse response = registrationService.createRegistration(request);

        Registration savedRegistration = registrationCaptor.getValue();
        assertNotNull(response);
        assertEquals(RegistrationStatus.REGISTERED, savedRegistration.getStatus());
        assertEquals(user, savedRegistration.getUser());
        assertEquals(event, savedRegistration.getEvent());

        verify(registrationValidator).validate(eq(savedRegistration), any(Instant.class));
        verify(registrationRepository).save(savedRegistration);
        verify(modelMapper).map(mappedRegistration, RegistrationResponse.class);
    }

    @Test
    void createRegistration_whenGdprIsNull_setsGdprToFalseAndKeepsPhotoConsentNull() {
        RegistrationRequest request = buildRequest();
        Registration mappedRegistration = new Registration();
        User user = createUser(1);
        Event event = createEvent(2);

        mappedRegistration.setGdpr(null);
        mappedRegistration.setPhotoConsent(null);

        when(modelMapper.map(request, Registration.class)).thenReturn(mappedRegistration);
        when(userService.findById(1)).thenReturn(user);
        when(eventService.findEventById(2)).thenReturn(event);
        when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(mappedRegistration, RegistrationResponse.class))
                .thenReturn(new RegistrationResponse(1, 2));

        registrationService.createRegistration(request);

        assertFalse(mappedRegistration.getGdpr());
        assertEquals(null, mappedRegistration.getPhotoConsent());
        verify(registrationValidator).validate(eq(mappedRegistration), any(Instant.class));
    }

    @Test
    void createRegistration_whenGdprAndPhotoConsentAreProvided_keepsProvidedValues() {
        RegistrationRequest request = buildRequest();
        Registration mappedRegistration = new Registration();
        User user = createUser(1);
        Event event = createEvent(2);

        mappedRegistration.setGdpr(true);
        mappedRegistration.setPhotoConsent(true);

        when(modelMapper.map(request, Registration.class)).thenReturn(mappedRegistration);
        when(userService.findById(1)).thenReturn(user);
        when(eventService.findEventById(2)).thenReturn(event);
        when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(mappedRegistration, RegistrationResponse.class))
                .thenReturn(new RegistrationResponse(1, 2));

        registrationService.createRegistration(request);

        assertTrue(mappedRegistration.getGdpr());
        assertTrue(mappedRegistration.getPhotoConsent());
    }

    @Test
    void createRegistration_whenValidatorRejectsRequest_propagatesExceptionAndDoesNotSave() {
        RegistrationRequest request = buildRequest();
        Registration mappedRegistration = new Registration();
        User user = createUser(1);
        Event event = createEvent(2);

        when(modelMapper.map(request, Registration.class)).thenReturn(mappedRegistration);
        when(userService.findById(1)).thenReturn(user);
        when(eventService.findEventById(2)).thenReturn(event);
        ValidationException validationException = new ValidationException("photoConsent",
                "Photo consent must be acknowledged.");
        org.mockito.Mockito.doThrow(validationException)
                .when(registrationValidator)
                .validate(any(Registration.class), any(Instant.class));

        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> registrationService.createRegistration(request));

        assertEquals("photoConsent", thrown.getField());
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void getRegistration_whenRegistrationExists_returnsMappedDetails() {
        Registration registration = buildExistingRegistration();

        when(registrationRepository.findByUserIdAndEventIdAndStatus(1, 2, RegistrationStatus.REGISTERED))
                .thenReturn(Optional.of(registration));

        RegistrationDetailsResponse response = registrationService.getRegistration(2, 1);

        assertEquals(registration.getDate(), response.getDate());
        assertEquals(FoodPreference.VEGAN, response.getFoodPreference());
        assertEquals(3, response.getAccommodationDays());
        assertTrue(response.getGdpr());
        assertTrue(response.getPhotoConsent());
        assertEquals(1, response.getUserId());
        assertEquals(2, response.getEventId());
        assertEquals("Jane Driver", response.getDriverName());
        assertEquals("0700000000", response.getDriverPhone());
        assertTrue(response.getEditable());
    }

    @Test
    void getRegistration_whenRegistrationPeriodHasEnded_marksResponseAsNotEditable() {
        Registration registration = buildExistingRegistration();
        registration.getEvent().setRegistrationEnd(Instant.now().minusSeconds(86400));

        when(registrationRepository.findByUserIdAndEventIdAndStatus(1, 2, RegistrationStatus.REGISTERED))
                .thenReturn(Optional.of(registration));

        assertFalse(registrationService.getRegistration(2, 1).getEditable());
    }

    @Test
    void getRegistration_whenRegistrationIsMissingOrWithdrawn_throwsNotFoundException() {
        when(registrationRepository.findByUserIdAndEventIdAndStatus(1, 2, RegistrationStatus.REGISTERED))
                .thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> registrationService.getRegistration(2, 1));

        assertEquals("Registration for user 1 and event 2 not found", thrown.getMessage());
    }

    @Test
    void updateRegistration_whenRequestIsValid_updatesEveryFieldAndSaves() {
        Registration registration = buildExistingRegistration();
        RegistrationRequest request = buildRequest();
        request.setFoodPreference(FoodPreference.VEGETARIAN);
        request.setAccommodationDays(5);
        request.setGdpr(true);
        request.setPhotoConsent(true);
        request.setDriverName("John Driver");
        request.setDriverPhone("0711111111");

        when(registrationRepository.findByUserIdAndEventIdAndStatus(1, 2, RegistrationStatus.REGISTERED))
                .thenReturn(Optional.of(registration));

        RegistrationDetailsResponse response = registrationService.updateRegistration(request);

        assertEquals(request.getDate(), registration.getDate());
        assertEquals(FoodPreference.VEGETARIAN, registration.getFoodPreference());
        assertEquals(5, registration.getAccommodationDays());
        assertTrue(registration.getGdpr());
        assertTrue(registration.getPhotoConsent());
        assertEquals("John Driver", registration.getDriverName());
        assertEquals("0711111111", registration.getDriverPhone());
        assertEquals(RegistrationStatus.REGISTERED, registration.getStatus());

        assertEquals(FoodPreference.VEGETARIAN, response.getFoodPreference());
        assertTrue(response.getEditable());

        verify(registrationValidator).validateUpdate(eq(registration), any(Instant.class));
        verify(registrationRepository).save(registration);
    }

    @Test
    void updateRegistration_whenGdprIsNull_setsGdprToFalseAndKeepsPhotoConsentNull() {
        Registration registration = buildExistingRegistration();
        RegistrationRequest request = buildRequest();

        when(registrationRepository.findByUserIdAndEventIdAndStatus(1, 2, RegistrationStatus.REGISTERED))
                .thenReturn(Optional.of(registration));

        registrationService.updateRegistration(request);

        assertFalse(registration.getGdpr());
        assertEquals(null, registration.getPhotoConsent());
    }

    @Test
    void updateRegistration_whenRegistrationIsMissingOrWithdrawn_throwsNotFoundException() {
        RegistrationRequest request = buildRequest();

        when(registrationRepository.findByUserIdAndEventIdAndStatus(1, 2, RegistrationStatus.REGISTERED))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> registrationService.updateRegistration(request));

        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void updateRegistration_whenRegistrationPeriodHasEnded_propagatesExceptionAndDoesNotSave() {
        Registration registration = buildExistingRegistration();
        RegistrationRequest request = buildRequest();

        when(registrationRepository.findByUserIdAndEventIdAndStatus(1, 2, RegistrationStatus.REGISTERED))
                .thenReturn(Optional.of(registration));
        org.mockito.Mockito.doThrow(new RegistrationClosedException(
                "Registration period has ended. The registration can only be deleted."))
                .when(registrationValidator)
                .validateUpdate(any(Registration.class), any(Instant.class));

        RegistrationClosedException thrown = assertThrows(
                RegistrationClosedException.class,
                () -> registrationService.updateRegistration(request));

        assertEquals("Registration period has ended. The registration can only be deleted.", thrown.getMessage());
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void deleteRegistration_whenRegistrationExists_setsStatusToWithdrawnAndSaves() {
        Registration registration = buildExistingRegistration();

        when(registrationRepository.findByUserIdAndEventIdAndStatus(1, 2, RegistrationStatus.REGISTERED))
                .thenReturn(Optional.of(registration));

        registrationService.deleteRegistration(2, 1);

        assertEquals(RegistrationStatus.WITHDRAWN, registration.getStatus());
        verify(registrationRepository).save(registration);
        verify(registrationRepository, never()).delete(any(Registration.class));
    }

    @Test
    void deleteRegistration_whenRegistrationIsMissingOrWithdrawn_throwsNotFoundException() {
        when(registrationRepository.findByUserIdAndEventIdAndStatus(1, 2, RegistrationStatus.REGISTERED))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> registrationService.deleteRegistration(2, 1));

        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void deleteRegistration_whenRegistrationPeriodHasEnded_stillWithdrawsRegistration() {
        Registration registration = buildExistingRegistration();
        registration.getEvent().setRegistrationEnd(Instant.now().minusSeconds(86400));

        when(registrationRepository.findByUserIdAndEventIdAndStatus(1, 2, RegistrationStatus.REGISTERED))
                .thenReturn(Optional.of(registration));

        registrationService.deleteRegistration(2, 1);

        assertEquals(RegistrationStatus.WITHDRAWN, registration.getStatus());
        verify(registrationRepository).save(registration);
    }

    private Registration buildExistingRegistration() {
        Registration registration = new Registration();
        registration.setId(10);
        registration.setStatus(RegistrationStatus.REGISTERED);
        registration.setDate(Instant.parse("2026-08-20T09:00:00Z"));
        registration.setFoodPreference(FoodPreference.VEGAN);
        registration.setAccommodationDays(3);
        registration.setGdpr(true);
        registration.setPhotoConsent(true);
        registration.setDriverName("Jane Driver");
        registration.setDriverPhone("0700000000");
        registration.setUser(createUser(1));
        registration.setEvent(createEvent(2));
        return registration;
    }

    private RegistrationRequest buildRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setDate(Instant.parse("2026-09-01T10:00:00Z"));
        request.setFoodPreference(FoodPreference.NONE);
        request.setAccommodationDays(2);
        request.setGdpr(null);
        request.setPhotoConsent(null);
        request.setUserId(1);
        request.setEventId(2);
        request.setDriverName("Jane Driver");
        request.setDriverPhone("0700000000");
        return request;
    }

    private User createUser(Integer id) {
        User user = new User();
        user.setId(id);
        user.setLocation(Location.CLUJ_NAPOCA);
        return user;
    }

    private Event createEvent(Integer id) {
        Event event = new Event();
        event.setId(id);
        event.setType(EventType.EXTERNAL);
        event.setStatus(EventStatus.PUBLISHED);
        event.setLocation(Location.CLUJ_NAPOCA);
        event.setRegistrationStart(Instant.now().minusSeconds(86400));
        event.setRegistrationEnd(Instant.now().plusSeconds(86400));
        return event;
    }
}
