package com.example.demo.validator;

import java.util.Arrays;
import java.util.Base64;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageTypeValidator implements ConstraintValidator<ImageType, String> {

    private String[] allowedTypes;

    @Override
    public void initialize(ImageType constraintAnnotation) {
        this.allowedTypes = constraintAnnotation.allowedTypes();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        final byte[] image;

        try {
            image = Base64.getDecoder().decode(value);
        } catch (IllegalArgumentException exception) {
            return false;
        }

        String fileType = getFileType(image);
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(fileType)) {
                return true;
            }
        }
        return false;
    }

    private String getFileType(byte[] value) {
       byte[] jpegSignature = {
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF
        };

        byte[] pngSignature = {
                (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
                (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
        };

        if (startsWith(value, jpegSignature)) {
            return "image/jpeg";
        }

        if (startsWith(value, pngSignature)) {
            return "image/png";
        }

        return "unknown";
    }

    private boolean startsWith(byte[] value, byte[] prefix) {
        if (value.length < prefix.length) {
            return false;
        }

        for (int index = 0; index < prefix.length; index++) {
            if (value[index] != prefix[index]) {
                return false;
            }
        }

        return true;
    }
}