package com.example.demo.dto.request;

import com.example.demo.model.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserRoleRequest {

    @NotNull(message = "User role is required")
    private UserRole userRole;
}