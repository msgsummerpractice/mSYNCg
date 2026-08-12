package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String location;
    private Boolean status;
    private String imageUrlString;
    private String role;
}
