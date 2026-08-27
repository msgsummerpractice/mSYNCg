package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Location {
    CLUJ_NAPOCA("Cluj-Napoca"),
    TARGU_MURES("Târgu Mureș"),
    TIMISOARA("Timișoara"),
    ALL("All");

    private final String displayValue;

    Location(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue
    public String getDisplayValue() {
        return displayValue;
    }

    @JsonCreator
    public static Location fromValue(String value) {
        for (Location location : values()) {
            if (location.displayValue.equalsIgnoreCase(value) || location.name().equalsIgnoreCase(value)) {
                return location;
            }
        }
        throw new IllegalArgumentException("Unknown location: " + value);
    }
}
