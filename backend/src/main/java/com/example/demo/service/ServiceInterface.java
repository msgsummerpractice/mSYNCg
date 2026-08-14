package com.example.demo.service;

import com.example.demo.dto.response.UserListResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.model.User;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.dto.request.UserRequest;

public interface ServiceInterface {
    UserResponse createUser(UserRequest user);
    public Page<UserListResponse> getUsers(Specification<User> spec, Pageable pageable);
}
