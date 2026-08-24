package com.example.demo.exceptions;

public class AlreadyCheckedInException extends RuntimeException {

    public AlreadyCheckedInException() {
        super("User has already checked in for this event.");
    }
}