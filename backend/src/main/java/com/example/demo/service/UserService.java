package com.example.demo.service;

import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.example.demo.exceptions.ValidationException;
import com.example.demo.filtering.users.UserSpec;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.dto.response.UserViewResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.dto.request.UserRequest;

@RequiredArgsConstructor
@Service
public class UserService implements UserServiceInterface<UserRequest, UserResponse, UserViewResponse, UserSpec> {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserRequest user) {

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

    @Override
    public Page<UserViewResponse> getAll(UserSpec spec, Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(spec,pageable);

        return usersPage.map(user -> modelMapper.map(user, UserViewResponse.class));
    }

}
