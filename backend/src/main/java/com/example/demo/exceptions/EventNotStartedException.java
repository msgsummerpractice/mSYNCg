package com.example.demo.exceptions;

public class EventNotStartedException extends RuntimeException {

    public EventNotStartedException() {
        super("Check-in is not allowed because the event has not started yet.");
    }
}
