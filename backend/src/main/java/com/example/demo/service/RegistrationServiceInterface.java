package com.example.demo.service;

import com.example.demo.dto.request.RegistrationRequest;
import com.example.demo.dto.response.RegistrationResponse;

public interface RegistrationServiceInterface {
    RegistrationResponse createRegistration(RegistrationRequest request);
}
