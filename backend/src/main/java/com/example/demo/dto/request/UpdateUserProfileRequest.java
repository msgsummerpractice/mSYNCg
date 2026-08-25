package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.example.demo.model.Location;
import com.example.demo.model.UserRole;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    private String firstName;

    private String lastName;

    private String email;

    private Location location;

    private UserRole role;

    private String imageBase64;

}
