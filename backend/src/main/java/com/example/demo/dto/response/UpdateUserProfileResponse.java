package com.example.demo.dto.response;

import com.example.demo.model.UserRole;
import com.example.demo.model.Location;

import lombok.Data;

@Data
public class UpdateUserProfileResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Location location;
    private UserRole role;
    private Boolean status;
    private String imageUrlString;
}
