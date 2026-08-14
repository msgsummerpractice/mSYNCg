package com.example.demo.service;

import com.example.demo.dto.response.UserViewResponse;
import com.example.demo.filtering.users.UserSpec;
import com.example.demo.dto.response.UserResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.example.demo.dto.request.UserRequest;

public interface ServiceInterface {
    UserResponse createUser(UserRequest user);
    Page<UserViewResponse> getUsers(UserSpec spec, Pageable pageable);
}
