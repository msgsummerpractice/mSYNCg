package com.example.demo.service;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.dto.request.UserRequest;

public interface ServiceInterface {
    UserResponse createUser(UserRequest user);

}
