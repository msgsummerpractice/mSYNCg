package com.example.demo.exceptions;

public class InvalidCheckInException extends RuntimeException {

    public InvalidCheckInException() {
        super("Invalid check-in code.");
    }
}