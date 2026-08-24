package com.example.demo.exceptions;

public class RegistrationClosedException extends RuntimeException {

    public RegistrationClosedException(String message) {
        super(message);
    }
}
