
package com.example.demo.service;

import com.example.demo.dto.request.LogInRequest;

public interface AuthService {
    String login(LogInRequest logInRequest);
}
