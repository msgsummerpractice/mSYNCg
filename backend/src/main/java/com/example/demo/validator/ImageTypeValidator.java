package com.example.demo.validator;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageTypeValidator implements ConstraintValidator<ImageType, byte[]> {

    private String[] allowedTypes;

    @Override
    public void initialize(ImageType constraintAnnotation) {
        this.allowedTypes = constraintAnnotation.allowedTypes();
    }

    @Override
    public boolean isValid(byte[] value, ConstraintValidatorContext context) {
        if (value == null || value.length == 0) {
            return true;
        }
        String fileType = getFileType(value);
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(fileType)) {
                return true;
            }
        }
        return false;
    }

    private String getFileType(byte[] value) {
        if (value.length < 8) {
            return "unknown";
        }

        if (Arrays.mismatch(value, new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF }) == -1) {
            return "image/jpeg"; 
        } else if (Arrays.mismatch(value, new byte[] { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D,
                (byte) 0x0A, (byte) 0x1A, (byte) 0x0A }) == -1) {
            return "image/png"; 
        }
        else {
             return "unknown";
        }

    }

}
