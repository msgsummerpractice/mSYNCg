package com.example.demo.service;

import com.example.demo.dto.request.RegistrationRequest;
import com.example.demo.dto.response.RegistrationResponse;
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
import java.time.LocalDateTime;
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

        verify(registrationValidator).validate(eq(savedRegistration), any(LocalDateTime.class));
        verify(registrationRepository).save(savedRegistration);
        verify(modelMapper).map(mappedRegistration, RegistrationResponse.class);
    }

    @Test
    void createRegistration_whenGdprAndPhotoConsentAreNull_setsBothToFalse() {
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
        assertFalse(mappedRegistration.getPhotoConsent());
        verify(registrationValidator).validate(eq(mappedRegistration), any(LocalDateTime.class));
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
                .validate(any(Registration.class), any(LocalDateTime.class));

        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> registrationService.createRegistration(request));

        assertEquals("photoConsent", thrown.getField());
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    private RegistrationRequest buildRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setDate(LocalDateTime.of(2026, 9, 1, 10, 0));
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
        event.setRegistrationStart(LocalDateTime.now().minusDays(1));
        event.setRegistrationEnd(LocalDateTime.now().plusDays(1));
        return event;
    }
}