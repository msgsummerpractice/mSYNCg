package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationLookupRequest {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Event ID is required")
    private Integer eventId;
}
