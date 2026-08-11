package main.java.com.example.demo.model;

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
