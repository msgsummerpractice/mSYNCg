package com.example.demo.controller;

import com.example.demo.dto.request.UpdateUserRoleRequest;
import com.example.demo.dto.request.UpdateUserStatusRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.dto.response.UserViewResponse;
import com.example.demo.filtering.users.UserSpec;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserViewResponse>> getUsers(
            UserSpec userSpec,
            Pageable pageable) {

        Page<UserViewResponse> response =
                userService.getUsers(userSpec, pageable);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRoleRequest request) {

        UserResponse userResponse =
                userService.updateUserRole(id, request.getUserRole());

        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        UserResponse userResponse =
                userService.updateUserStatus(id, request.getStatus());

        return ResponseEntity.ok(userResponse);
    }
}