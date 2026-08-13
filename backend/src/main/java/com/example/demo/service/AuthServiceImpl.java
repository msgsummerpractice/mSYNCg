
package com.example.demo.service;

import org.springframework.security.core.AuthenticationException;
import com.example.demo.exceptions.UnathorizedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.LogInRequest;
import com.example.demo.security.JWTokenProvider;
import com.example.demo.security.UserDetailService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailService userDetailService;

    @Autowired
    private JWTokenProvider jwtTokenProvider;

    @Override
    public String login(LogInRequest logInRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDetailService.loadUserByUsername(logInRequest.getEmail()),
                            logInRequest.getPassword(),
                            userDetailService.loadUserByUsername(logInRequest.getEmail()).getAuthorities()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);
            return token;
        } catch (AuthenticationException e) {
            throw new UnathorizedException("Invalid email or password");
        }

    }
}
