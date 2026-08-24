package com.example.demo.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.example.demo.dto.response.CheckInResponse;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.ValidationException;
import com.example.demo.model.CheckIn;
import com.example.demo.model.Event;
import com.example.demo.model.EventStatus;

import org.springframework.stereotype.Service;

import com.example.demo.repository.CheckInRepository;
import com.example.demo.repository.EventRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.WriterException;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckInService implements CheckInServiceInterface {

    private final EventRepository eventRepository;
    private final CheckInRepository checkInRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public CheckInResponse generateCodesForEvent(Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event", eventId));

        if (EventStatus.PUBLISHED != event.getStatus()) {
            throw new ValidationException("Event must be published to generate check-in codes.");
        }

        if (checkInRepository.findByEventId(eventId).isPresent()) {
            throw new ValidationException("Check-in codes have already been generated for this event.");
        }

        int randomNum = 100000 + secureRandom.nextInt(900000);

        String qrPayload = "Event:" + event.getName() + "|ID:" + event.getId();

        String base64QrImage = generateBase64QrCode(qrPayload, 300, 300);

        CheckIn checkIn = new CheckIn();
        checkIn.setEvent(event);
        checkIn.setQrCode(qrPayload);
        checkIn.setCode((long) randomNum);
        checkInRepository.save(checkIn);

        return new CheckInResponse(base64QrImage, String.valueOf(randomNum));
    }

    @Override
    public Optional<CheckInResponse> getCodesForEvent(Integer eventId) {
        return checkInRepository.findByEventId(eventId)
                .map(checkIn -> new CheckInResponse(
                        generateBase64QrCode(checkIn.getQrCode(), 300, 300),
                        String.valueOf(checkIn.getCode())));
    }

    @Override
    public String generateBase64QrCode(String text, int width, int height) {
        if (text == null || text.isBlank()) {
            throw new ValidationException("QR code text must not be blank.");
        }
        if (width <= 0 || height <= 0) {
            throw new ValidationException("QR code dimensions must be positive.");
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        } catch (WriterException | IOException exception) {
            throw new IllegalStateException("Failed to generate QR code.", exception);
        }
    }

}
