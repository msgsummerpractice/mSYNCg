package com.example.demo.dto.response;

import com.example.demo.model.UserRole;
import com.example.demo.model.Location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserProfileResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Location location;
    private UserRole role;
    private Boolean status;
    private String imageMimeType;
    private String imageBase64;
}
