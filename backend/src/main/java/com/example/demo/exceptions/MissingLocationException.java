package com.example.demo.exceptions;

public class MissingLocationException extends RuntimeException {
    public MissingLocationException(String message) {
        super(message);
    }
}
