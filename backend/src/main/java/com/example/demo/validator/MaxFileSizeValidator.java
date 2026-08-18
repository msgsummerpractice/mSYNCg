package com.example.demo.validator;
 
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
 
public class MaxFileSizeValidator implements ConstraintValidator<MaxFileSize, byte[]> {
 
    private long maxFileSize;
 
    @Override
    public void initialize(MaxFileSize constraintAnnotation) {
        this.maxFileSize = constraintAnnotation.value();
    }
 
    @Override
    public boolean isValid(byte[] value, ConstraintValidatorContext context) {
        if (value == null || value.length == 0) {
            return true;
        }
        return value.length <= maxFileSize;
    }
 
}