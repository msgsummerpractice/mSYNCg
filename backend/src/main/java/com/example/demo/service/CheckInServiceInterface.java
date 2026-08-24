package com.example.demo.service;

import com.example.demo.dto.response.CheckInResponse;

public interface CheckInServiceInterface {
    CheckInResponse generateCodesForEvent(Integer eventId);

    String generateBase64QrCode(String text, int width, int height);
}
