package com.example.demo.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserViewResponse;
import com.example.demo.filtering.users.UserSpec;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


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
}
    