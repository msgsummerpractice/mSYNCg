package com.example.demo.dto.request;

import com.example.demo.model.Location;
import com.example.demo.model.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserProfileRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Location is required")
    private Location location;

    @NotNull(message = "User role is required")
    private UserRole role;

    private String imageBase64;

}
