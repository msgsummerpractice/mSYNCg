package com.example.demo.service;

import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.example.demo.exceptions.ValidationException;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.dto.request.UserRequest;

@RequiredArgsConstructor
@Service
public class UserService implements ServiceInterface {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("email", "Email address is already in use.");
        }

        User newUser = this.modelMapper.map(user, User.class);

        newUser.setPassword(this.passwordEncoder.encode(user.getPassword()));
        newUser.setStatus(true);
        newUser.setRole(UserRole.PARTICIPANT);

        userRepository.save(newUser);

        return modelMapper.map(newUser, UserResponse.class);
    }

}
