package com.example.demo.service;

import com.example.demo.repository.UserRepository;
import com.example.demo.exceptions.ValidationException;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.dto.request.UserRequest;

@Service
public class UserService implements ServiceInterface {

    private final UserRepository userRepository;
    private ModelMapper modelMapper;
    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserResponse createUser(UserRequest user) {
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new ValidationException("password", "Password cannot be null or blank.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("email", "Email address is already in use.");
        }

        User newUser = this.modelMapper.map(user, User.class);

        newUser.setPassword(this.passwordEncoder.encode(user.getPassword()));
        newUser.setStatus(true);
        newUser.setRole(UserRole.PARTICIPANT);

        this.userRepository.save(newUser);

        return this.modelMapper.map(newUser, UserResponse.class);
    }

}
