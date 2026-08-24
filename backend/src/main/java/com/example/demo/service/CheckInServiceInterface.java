package com.example.demo.service;

import java.util.Optional;

import com.example.demo.dto.response.CheckInResponse;

public interface CheckInServiceInterface {
    CheckInResponse generateCodesForEvent(Integer eventId);

    Optional<CheckInResponse> getCodesForEvent(Integer eventId);

    String generateBase64QrCode(String text, int width, int height);
}
