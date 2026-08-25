package com.example.demo.dto.request;

import com.example.demo.model.Location;
import com.example.demo.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserProfileRequest {

    private String firstName;

    private String lastName;

    private String email;

    private Location location;

    private UserRole role;

    private String imageBase64;

}
