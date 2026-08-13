
package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.LogInRequest;
import com.example.demo.provider.JWTokenProvider;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailService userDetailService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserDetailService userDetailService) {
        this.authenticationManager = authenticationManager;
        this.userDetailService = userDetailService;
    }

    @Autowired
    private JWTokenProvider jwtTokenProvider;

    @Override
    public String login(LogInRequest logInRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDetailService.loadUserByUsername(logInRequest.getEmail()),
                        logInRequest.getPassword(),
                        userDetailService.loadUserByUsername(logInRequest.getEmail()).getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return token;
    }
}
