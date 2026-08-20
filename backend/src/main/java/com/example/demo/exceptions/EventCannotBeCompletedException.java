package com.example.demo.exceptions;

public class EventCannotBeCompletedException extends RuntimeException {

    public EventCannotBeCompletedException(String message) {
        super(message);
    }
}