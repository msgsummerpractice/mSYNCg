package com.example.demo.service;

import com.example.demo.dto.request.RegistrationRequest;
import com.example.demo.dto.response.RegistrationDetailsResponse;
import com.example.demo.dto.response.RegistrationResponse;

public interface RegistrationServiceInterface {
    RegistrationResponse createRegistration(RegistrationRequest request);

    RegistrationDetailsResponse getRegistration(Integer eventId, Integer userId);

    RegistrationDetailsResponse updateRegistration(RegistrationRequest request);

    void deleteRegistration(Integer eventId, Integer userId);
}
