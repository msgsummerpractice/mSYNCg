package com.example.demo.controller;
import org.springframework.security.core.Authentication;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.dto.response.UserViewResponse;
import com.example.demo.exceptions.CannotChangeOwnRoleException;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.exceptions.NotFoundException;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
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

    @Mock
    private Authentication authentication;

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
    void getUsers_returnsPageOfUsers() throws Exception {
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

       when(userService.getAll(
        notNull(),
        eq(PageRequest.of(0, 20))
        )).thenReturn(page);

        mockMvc.perform(get("/api/admin/users")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value("ada@example.com"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(userService).getAll(
        notNull(),
        eq(PageRequest.of(0, 20))
        );
    }

    @Test
    void updateUserRole_returnsOk_withUpdatedUser() throws Exception {
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

                when(authentication.getName())
                        .thenReturn("authenticated@example.com");

                when(userService.updateUserRole(
                        3,
                        UserRole.ADMIN,
                        "authenticated@example.com"
                )).thenReturn(response);

                mockMvc.perform(patch("/api/admin/users/3/role")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "userRole": "Admin"
                                }
                                """))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(3))
                        .andExpect(jsonPath("$.role").value(UserRole.ADMIN.name()));

                verify(userService).updateUserRole(
                        3,
                        UserRole.ADMIN,
                        "authenticated@example.com"
                );
    }

    @Test
    void updateUserStatus_returnsOk_withUpdatedUser() throws Exception {
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

    @Test
    void updateUserRole_whenUserDoesNotExist_returnsNotFound() throws Exception {
            when(authentication.getName())
                    .thenReturn("authenticated@example.com");

            when(userService.updateUserRole(
                    99,
                    UserRole.ADMIN,
                    "authenticated@example.com"
            )).thenThrow(new NotFoundException("User",99));

            mockMvc.perform(patch("/api/admin/users/99/role")
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                            "userRole": "Admin"
                            }
                            """))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("User with id 99 not found"));

            verify(userService).updateUserRole(
                    99,
                    UserRole.ADMIN,
                    "authenticated@example.com"
            );
    }
    @Test
    void updateUserStatus_whenUserDoesNotExist_returnsNotFound() throws Exception {
                when(userService.updateUserStatus(99, false))
                    .thenThrow(new NotFoundException("User", 99));

            mockMvc.perform(patch("/api/admin/users/99/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                            "status": false
                            }
                            """))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("User with id 99 not found"));

            verify(userService).updateUserStatus(99, false);
    }

    @Test
    void updateUserRole_whenAdminChangesOwnRole_returnsBadRequest() throws Exception {
        when(authentication.getName())
                .thenReturn("admin@example.com");

        when(userService.updateUserRole(
                3,
                UserRole.PARTICIPANT,
                "admin@example.com"
        )).thenThrow(new CannotChangeOwnRoleException());

        mockMvc.perform(patch("/api/admin/users/3/role")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "userRole": "Participant"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Admin cannot change their own role."));

        verify(userService).updateUserRole(
                3,
                UserRole.PARTICIPANT,
                "admin@example.com"
        );
    }   


}