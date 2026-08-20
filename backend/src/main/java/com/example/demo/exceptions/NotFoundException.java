package com.example.demo.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String entityName, Integer id) {
        super(entityName + " with id " + id + " not found");
    }
}
