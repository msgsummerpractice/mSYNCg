package com.example.demo.service;

import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import com.example.demo.model.Location;
import com.example.demo.model.UserRole;
import com.example.demo.dto.request.UpdateUserProfileRequest;
import com.example.demo.exceptions.CannotChangeOwnRoleException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.ValidationException;
import com.example.demo.filtering.users.UserSpec;

import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import com.example.demo.dto.response.UserViewResponse;
import com.example.demo.dto.response.UpdateUserProfileResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.model.User;
import com.example.demo.dto.request.UserRequest;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

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
        Page<User> usersPage = userRepository.findAll(spec, pageable);

        return usersPage.map(user -> modelMapper.map(user, UserViewResponse.class));
    }

    public UserResponse updateUserRole(Integer id, UserRole userRole, String authenticatedEmail) {

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

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllByLocation(Location location) {
        return userRepository.findAllByLocation(location);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UpdateUserProfileResponse updateProfile(Integer id, UpdateUserProfileRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id));

        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setLocation(userRequest.getLocation());
        user.setRole(userRequest.getRole());

        if (userRequest.getImageBase64() != null && !userRequest.getImageBase64().isBlank()) {
            user.setImage(Base64.getDecoder().decode(userRequest.getImageBase64()));
        }
        User updatedUser = userRepository.save(user);

        return toProfileResponse(updatedUser);
    }

    public UpdateUserProfileResponse getProfile(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", id));

        return toProfileResponse(user);
    }

    private UpdateUserProfileResponse toProfileResponse(User user) {
        UpdateUserProfileResponse response = new UpdateUserProfileResponse();

        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setLocation(user.getLocation());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());

        if (user.getImage() != null && user.getImage().length > 0) {
            response.setImageBase64(
                    Base64.getEncoder().encodeToString(user.getImage()));
            response.setImageMimeType(detectImageMimeType(user.getImage()));
        }

        return response;
    }

    private String detectImageMimeType(byte[] image) {
        if (image.length >= 8
                && image[0] == (byte) 0x89
                && image[1] == 0x50
                && image[2] == 0x4E
                && image[3] == 0x47) {
            return "image/png";
        }

        if (image.length >= 3
                && image[0] == (byte) 0xFF
                && image[1] == (byte) 0xD8
                && image[2] == (byte) 0xFF) {
            return "image/jpeg";
        }

        return "application/octet-stream";
    }

    public User findById(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new NotFoundException("User", id);
        }
    }

}
