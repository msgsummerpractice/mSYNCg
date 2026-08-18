
package com.example.demo.service;

import com.example.demo.dto.request.LogInRequest;
import com.example.demo.dto.response.CurrentUserResponse;

public interface AuthService {
    String login(LogInRequest logInRequest);
    CurrentUserResponse getCurrentUser(String email);
}
