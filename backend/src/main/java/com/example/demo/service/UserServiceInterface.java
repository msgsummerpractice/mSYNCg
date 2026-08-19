package com.example.demo.service;

import org.springframework.data.domain.Pageable;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.dto.response.UserViewResponse;
import com.example.demo.filtering.users.UserSpec;

import org.springframework.data.domain.Page;

public interface UserServiceInterface {
    UserResponse create(UserRequest request);

    Page<UserViewResponse> getAll(UserSpec spec, Pageable pageable);
}
