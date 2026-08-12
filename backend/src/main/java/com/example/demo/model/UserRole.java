package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    PARTICIPANT("Participant"),
    MARKETING_ORGANIZER("Marketing Organizer"),
    HR_USER("HR User"),
    ADMIN("Admin");

    private final String displayValue;

    UserRole(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue
    public String getDisplayValue() {
        return displayValue;
    }
}
