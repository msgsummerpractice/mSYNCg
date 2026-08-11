package main.java.com.example.demo.model;

public enum Location {
    CLUJ_NAPOCA("Cluj-Napoca"),
    TARGU_MURES("Târgu Mureș"),
    TIMISOARA("Timișoara");

    private final String displayValue;

    Location(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue 
    public String getDisplayValue() {
        return displayValue;
    }
}
