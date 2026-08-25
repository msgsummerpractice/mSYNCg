package com.example.demo.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.request.UpdateUserProfileRequest;
import com.example.demo.dto.response.UserViewResponse;
import com.example.demo.filtering.users.UserSpec;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.dto.response.UpdateUserProfileResponse;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.create(userRequest);

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    public ResponseEntity<Page<UserViewResponse>> getUsers(
            UserSpec userSpec,
            Pageable pageable) {
        Page<UserViewResponse> response = userService.getAll(userSpec, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<UpdateUserProfileResponse> updateUserProfile(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserProfileRequest userRequest) {
        UpdateUserProfileResponse response = userService.updateProfile(id, userRequest);

        return ResponseEntity.ok(response);
    }

}
