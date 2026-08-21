package com.example.demo.validator;

import java.util.Base64;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class MaxFileSizeValidator implements ConstraintValidator<MaxFileSize, String> {

    private long maxFileSize;

    @Override
    public void initialize(MaxFileSize constraintAnnotation) {
        this.maxFileSize = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value)) {
            return true;
        }

        try {
            byte[] decodedImage = Base64.getDecoder().decode(value);
            return decodedImage.length <= maxFileSize;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

}
