package com.example.demo.dto.response;

import com.example.demo.model.Location;
import com.example.demo.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    private Location location;
    private Boolean status;
}
