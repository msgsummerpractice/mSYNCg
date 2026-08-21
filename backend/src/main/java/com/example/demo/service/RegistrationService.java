package com.example.demo.service;

import com.example.demo.repository.RegistrationRepository;
import com.example.demo.dto.request.RegistrationRequest;
import com.example.demo.dto.response.RegistrationResponse;
import com.example.demo.model.Registration;
import com.example.demo.model.RegistrationStatus;
import com.example.demo.validator.RegistrationValidator;
import java.time.LocalDateTime;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
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

        if (registration.getPhotoConsent() == null) {
            registration.setPhotoConsent(false);
        }

        registrationValidator.validate(registration, LocalDateTime.now());

        registration.setStatus(RegistrationStatus.REGISTERED);
        registrationRepository.save(registration);

        return modelMapper.map(registration, RegistrationResponse.class);
    }

}
