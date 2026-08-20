package com.example.demo.exceptions;

public class CannotChangeOwnRoleException extends RuntimeException {

    public CannotChangeOwnRoleException() {
        super("Admin cannot change their own role.");
    }
}