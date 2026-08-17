package com.example.demo.service;

import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.example.demo.exceptions.CannotChangeOwnRoleException;
import com.example.demo.exceptions.NotFoundException;
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

    @Override
    public Page<UserViewResponse> getUsers(UserSpec spec, Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(spec,pageable);

        return usersPage.map(user -> modelMapper.map(user, UserViewResponse.class));
    }

    public UserResponse updateUserRole( Integer id, UserRole userRole,String authenticatedEmail) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id));

        User authenticatedUser = userRepository.findByEmail(authenticatedEmail);

        if (user.getId().equals(authenticatedUser.getId())) {
            throw new CannotChangeOwnRoleException();
        }

        user.setRole(userRole);

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserResponse.class);
    }

    public UserResponse updateUserStatus(Integer id, Boolean status) {

        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User", id));

        user.setStatus(status);

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserResponse.class);
    }



}
