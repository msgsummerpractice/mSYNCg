package com.example.demo.exceptions;

public class UserNotRegisteredException extends RuntimeException {

    public UserNotRegisteredException() {
        super("User is not registered for this event.");
    }
}