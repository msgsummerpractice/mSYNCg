package com.example.demo.exceptions;

public class EventCheckInExpiredException extends RuntimeException {

    public EventCheckInExpiredException() {
        super("Check-in is not allowed because the event has already ended.");
    }
}