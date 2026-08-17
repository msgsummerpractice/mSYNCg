package com.example.demo.controller;

import com.example.demo.dto.request.UpdateUserRoleRequest;
import com.example.demo.dto.request.UpdateUserStatusRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.dto.response.UserViewResponse;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.filtering.users.UserSpec;
import com.example.demo.model.Location;
import com.example.demo.model.UserRole;
import com.example.demo.service.UserService;

import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setCustomArgumentResolvers(
                        new SpecificationArgumentResolver(),
                        new PageableHandlerMethodArgumentResolver()
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
        void getUsersReturnsPageOfUsers() throws Exception {
            UserViewResponse user = new UserViewResponse(
                1,
                "Ada",
                "Lovelace",
                "ada@example.com",
                UserRole.PARTICIPANT,
                Location.CLUJ_NAPOCA,
                true
    );

            Page<UserViewResponse> page =
                new PageImpl<>(List.of(user), PageRequest.of(0, 20), 1);

            when(userService.getUsers(any(UserSpec.class), any(Pageable.class)))
                .thenReturn(page);

            mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value("ada@example.com"))
                .andExpect(jsonPath("$.totalElements").value(1));

            verify(userService).getUsers(any(UserSpec.class), any(Pageable.class));
    }

    @Test
        void updateUserRoleReturnsOkWithUpdatedUser() throws Exception {
            UserResponse response = new UserResponse(
                3,
                "Ada",
                "Lovelace",
                "ada@example.com",
                Location.CLUJ_NAPOCA.name(),
                true,
                null,
                UserRole.ADMIN.name()
        );

        when(userService.updateUserRole(3, UserRole.ADMIN))
                .thenReturn(response);

        mockMvc.perform(patch("/api/admin/users/3/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "userRole": "Admin"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.role").value(UserRole.ADMIN.name()));

        verify(userService).updateUserRole(3, UserRole.ADMIN);
    }   

    @Test
    void updateUserStatusReturnsOkWithUpdatedUser() throws Exception {
        UserResponse response = new UserResponse(
                3,
                "Ada",
                "Lovelace",
                "ada@example.com",
                Location.CLUJ_NAPOCA.name(),
                false,
                null,
                UserRole.PARTICIPANT.name()
        );

        when(userService.updateUserStatus(3, false))
                .thenReturn(response);

        mockMvc.perform(patch("/api/admin/users/3/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "status": false
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.status").value(false));

        verify(userService).updateUserStatus(3, false);
    }
}