
package com.example.demo.service;

import org.springframework.security.core.AuthenticationException;
import com.example.demo.exceptions.AccountInactiveException;
import com.example.demo.exceptions.UnathorizedException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.LogInRequest;
import com.example.demo.dto.response.CurrentUserResponse;
import com.example.demo.security.JWTokenProvider;
import com.example.demo.security.UserDetailService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailService userDetailService;
    private final JWTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    @Override
    public String login(LogInRequest logInRequest) {

        User existingUser = userRepository.findByEmail(logInRequest.getEmail());
        if (existingUser != null && Boolean.FALSE.equals(existingUser.getStatus())) {
            throw new AccountInactiveException();
        }

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

     @Override
    public CurrentUserResponse getCurrentUser(String email) {

    User user = userRepository.findByEmail(email);

        return new CurrentUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
       
    }
}
