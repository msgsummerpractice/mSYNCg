package com.example.demo.exceptions;

public class EventAlreadyCompletedException extends RuntimeException {

    public EventAlreadyCompletedException() {
        super("Check-in is not allowed because the event is already completed.");
    }
}