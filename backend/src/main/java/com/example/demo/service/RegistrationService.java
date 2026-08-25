package com.example.demo.service;

import com.example.demo.repository.RegistrationRepository;
import com.example.demo.dto.request.RegistrationRequest;
import com.example.demo.dto.response.RegistrationDetailsResponse;
import com.example.demo.dto.response.RegistrationResponse;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.model.Registration;
import com.example.demo.model.RegistrationStatus;
import com.example.demo.validator.RegistrationValidator;
import java.time.LocalDateTime;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationService implements RegistrationServiceInterface {

    private final RegistrationRepository registrationRepository;
    private final ModelMapper modelMapper;
    private final RegistrationValidator registrationValidator;
    private final UserService userService;
    private final EventService eventService;

    public RegistrationResponse createRegistration(RegistrationRequest request) {
        Registration registration = modelMapper.map(request, Registration.class);
        registration.setUser(userService.findById(request.getUserId()));
        registration.setEvent(eventService.findEventById(request.getEventId()));

        if (registration.getGdpr() == null) {
            registration.setGdpr(false);
        }

        registrationValidator.validate(registration, LocalDateTime.now());

        registration.setStatus(RegistrationStatus.REGISTERED);
        registrationRepository.save(registration);

        return modelMapper.map(registration, RegistrationResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationDetailsResponse getRegistration(Integer eventId, Integer userId) {
        Registration registration = findRegistration(eventId, userId);
        return toDetailsResponse(registration, LocalDateTime.now());
    }

    @Override
    @Transactional
    public RegistrationDetailsResponse updateRegistration(RegistrationRequest request) {
        Registration registration = findRegistration(request.getEventId(), request.getUserId());

        registration.setDate(request.getDate());
        registration.setFoodPreference(request.getFoodPreference());
        registration.setAccommodationDays(request.getAccommodationDays());
        registration.setGdpr(Boolean.TRUE.equals(request.getGdpr()));
        registration.setPhotoConsent(request.getPhotoConsent());
        registration.setDriverName(request.getDriverName());
        registration.setDriverPhone(request.getDriverPhone());

        LocalDateTime now = LocalDateTime.now();
        registrationValidator.validateUpdate(registration, now);

        registrationRepository.save(registration);

        return toDetailsResponse(registration, now);
    }

    @Override
    @Transactional
    public void deleteRegistration(Integer eventId, Integer userId) {
        Registration registration = findRegistration(eventId, userId);
        registration.setStatus(RegistrationStatus.WITHDRAWN);
        registrationRepository.save(registration);
    }

    private Registration findRegistration(Integer eventId, Integer userId) {
        return registrationRepository
                .findByUserIdAndEventIdAndStatus(userId, eventId, RegistrationStatus.REGISTERED)
                .orElseThrow(() -> new NotFoundException(
                        "Registration for user " + userId + " and event " + eventId + " not found"));
    }

    private RegistrationDetailsResponse toDetailsResponse(Registration registration, LocalDateTime referenceTime) {
        RegistrationDetailsResponse response = new RegistrationDetailsResponse();
        response.setDate(registration.getDate());
        response.setFoodPreference(registration.getFoodPreference());
        response.setAccommodationDays(registration.getAccommodationDays());
        response.setGdpr(registration.getGdpr());
        response.setPhotoConsent(registration.getPhotoConsent());
        response.setUserId(registration.getUser().getId());
        response.setEventId(registration.getEvent().getId());
        response.setDriverName(registration.getDriverName());
        response.setDriverPhone(registration.getDriverPhone());
        response.setEditable(!referenceTime.isAfter(registration.getEvent().getRegistrationEnd()));
        return response;
    }

}
