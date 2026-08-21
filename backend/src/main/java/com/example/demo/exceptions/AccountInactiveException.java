package com.example.demo.exceptions;

public class AccountInactiveException extends RuntimeException {

    public AccountInactiveException() {
        super("Account is inactive. Please contact an administrator.");
    }
}
