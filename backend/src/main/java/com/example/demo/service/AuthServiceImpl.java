
package com.example.demo.service;

import org.springframework.security.core.AuthenticationException;
import com.example.demo.exceptions.AccountInactiveException;
import com.example.demo.exceptions.UnathorizedException;
import com.example.demo.exceptions.ValidationException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.LogInRequest;
import com.example.demo.dto.request.ResetPasswordRequest;
import com.example.demo.dto.response.CurrentUserResponse;
import com.example.demo.security.JWTokenProvider;
import com.example.demo.security.UserDetailService;
import com.example.demo.service.notification.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailService userDetailService;
    private final JWTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;
    private static final long RESET_TOKEN_EXPIRATION_MINUTES = 30;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public String login(LogInRequest logInRequest) {

        User existingUser = userRepository.findByEmail(logInRequest.getEmail());
        if (existingUser != null && Boolean.FALSE.equals(existingUser.getStatus())) {
            throw new AccountInactiveException();
        }

        UserDetails userDetails = userDetailService.loadUserByUsername(logInRequest.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            logInRequest.getPassword(),
                            userDetails.getAuthorities()));

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
                user.getRole());

    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            return;
        }

        String rawToken = passwordResetService.generateToken();
        String tokenHash = passwordResetService.hashToken(rawToken);

        user.setResetTokenHash(tokenHash);
        user.setResetTokenExpiresAt(
                Instant.now().plusSeconds(RESET_TOKEN_EXPIRATION_MINUTES * 60));

        userRepository.save(user);

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                rawToken);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        String tokenHash = passwordResetService.hashToken(request.getToken());

        User user = userRepository.findByResetTokenHash(tokenHash)
                .orElseThrow(() -> new ValidationException("token", "Invalid reset token."));

        Instant expiration = user.getResetTokenExpiresAt();

        if (expiration == null || !expiration.isAfter(Instant.now())) {
            throw new ValidationException("token", "Reset token has expired.");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        user.setResetTokenHash(null);
        user.setResetTokenExpiresAt(null);

        userRepository.save(user);
    }
}
