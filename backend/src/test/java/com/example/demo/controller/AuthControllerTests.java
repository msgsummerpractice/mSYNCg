package com.example.demo.controller;

import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.ResetPasswordRequest;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.exceptions.ValidationException;
import com.example.demo.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

	@Mock
	private AuthService authService;

	@InjectMocks
	private AuthController authController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(authController)
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	void forgotPassword_WhenEmailIsValid_ReturnsOk() throws Exception {
		ArgumentCaptor<ForgotPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ForgotPasswordRequest.class);
		doNothing().when(authService).forgotPassword(requestCaptor.capture());

		mockMvc.perform(post("/api/auth/forgot-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(forgotPasswordRequestJson("user@example.com")))
				.andExpect(status().isOk());

		verify(authService).forgotPassword(requestCaptor.capture());
		assertEquals("user@example.com", requestCaptor.getValue().getEmail());
	}

	@Test
	void forgotPassword_WhenEmailIsInvalid_ReturnsBadRequestWithFieldError() throws Exception {
		ArgumentCaptor<ForgotPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ForgotPasswordRequest.class);

		mockMvc.perform(post("/api/auth/forgot-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(forgotPasswordRequestJson("not-an-email")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors[0].field").value("email"));

		verify(authService, never()).forgotPassword(requestCaptor.capture());
	}

	@Test
	void forgotPassword_WhenEmailIsMissing_ReturnsBadRequestWithFieldError() throws Exception {
		ArgumentCaptor<ForgotPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ForgotPasswordRequest.class);

		mockMvc.perform(post("/api/auth/forgot-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors[0].field").value("email"));

		verify(authService, never()).forgotPassword(requestCaptor.capture());
	}

	@Test
	void forgotPassword_WhenServiceThrowsUnexpectedException_ReturnsInternalServerError() throws Exception {
		doThrow(new RuntimeException("Email service unavailable"))
				.when(authService).forgotPassword(any(ForgotPasswordRequest.class));

		mockMvc.perform(post("/api/auth/forgot-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(forgotPasswordRequestJson("user@example.com")))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.error").value("Internal Server Error"))
				.andExpect(jsonPath("$.message").value("Email service unavailable"));
	}

	@Test
	void resetPassword_WhenRequestIsValid_ReturnsOk() throws Exception {
		ArgumentCaptor<ResetPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ResetPasswordRequest.class);
		doNothing().when(authService).resetPassword(requestCaptor.capture());

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("valid-token-123", "NewP@ssw0rd")))
				.andExpect(status().isOk());

		verify(authService).resetPassword(requestCaptor.capture());
		assertEquals("valid-token-123", requestCaptor.getValue().getToken());
		assertEquals("NewP@ssw0rd", requestCaptor.getValue().getNewPassword());
	}

	@Test
	void resetPassword_WhenTokenIsInvalid_ReturnsBadRequestWithFieldError() throws Exception {
		doThrow(new ValidationException("token", "Invalid reset token."))
				.when(authService).resetPassword(any(ResetPasswordRequest.class));

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("invalid-token", "NewP@ssw0rd")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fieldErrors[0].field").value("token"))
				.andExpect(jsonPath("$.fieldErrors[0].reason").value("Invalid reset token."));
	}

	@Test
	void resetPassword_WhenTokenIsExpired_ReturnsBadRequestWithFieldError() throws Exception {
		doThrow(new ValidationException("token", "Reset token has expired."))
				.when(authService).resetPassword(any(ResetPasswordRequest.class));

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("expired-token", "NewP@ssw0rd")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fieldErrors[0].field").value("token"))
				.andExpect(jsonPath("$.fieldErrors[0].reason").value("Reset token has expired."));
	}

	@Test
	void resetPassword_WhenPasswordIsMissing_ReturnsBadRequestWithFieldError() throws Exception {
		ArgumentCaptor<ResetPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ResetPasswordRequest.class);

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("valid-token-123", "")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors[?(@.field == 'newPassword')]").exists());

		verify(authService, never()).resetPassword(requestCaptor.capture());
	}

	@Test
	void resetPassword_WhenTokenIsMissing_ReturnsBadRequestWithFieldError() throws Exception {
		ArgumentCaptor<ResetPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ResetPasswordRequest.class);

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("", "NewP@ssw0rd")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors[?(@.field == 'token')]").exists());

		verify(authService, never()).resetPassword(requestCaptor.capture());
	}

	@Test
	void resetPassword_WhenRequiredFieldsAreMissing_ReturnsBadRequest() throws Exception {
		ArgumentCaptor<ResetPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ResetPasswordRequest.class);

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Request validation failed"))
				.andExpect(jsonPath("$.fieldErrors.length()").value(2));

		verify(authService, never()).resetPassword(requestCaptor.capture());
	}

	@Test
	void resetPassword_WhenPasswordIsTooShort_ReturnsBadRequestWithFieldError() throws Exception {
		ArgumentCaptor<ResetPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ResetPasswordRequest.class);

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("valid-token-123", "Short1!")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors[?(@.field == 'newPassword')]").exists());

		verify(authService, never()).resetPassword(requestCaptor.capture());
	}

	@Test
	void resetPassword_WhenPasswordIsTooLong_ReturnsBadRequestWithFieldError() throws Exception {
		ArgumentCaptor<ResetPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ResetPasswordRequest.class);
		String longPassword = "A1@" + "a".repeat(62);

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("valid-token-123", longPassword)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors[?(@.field == 'newPassword')]").exists());

		verify(authService, never()).resetPassword(requestCaptor.capture());
	}

	@Test
	void resetPassword_WhenPasswordMissingUppercase_ReturnsBadRequestWithFieldError() throws Exception {
		ArgumentCaptor<ResetPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ResetPasswordRequest.class);

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("valid-token-123", "password123!")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors[?(@.field == 'newPassword')]").exists());

		verify(authService, never()).resetPassword(requestCaptor.capture());
	}

	@Test
	void resetPassword_WhenPasswordMissingLowercase_ReturnsBadRequestWithFieldError() throws Exception {
		ArgumentCaptor<ResetPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ResetPasswordRequest.class);

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("valid-token-123", "PASSWORD123!")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors[?(@.field == 'newPassword')]").exists());

		verify(authService, never()).resetPassword(requestCaptor.capture());
	}

	@Test
	void resetPassword_WhenPasswordMissingNumber_ReturnsBadRequestWithFieldError() throws Exception {
		ArgumentCaptor<ResetPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ResetPasswordRequest.class);

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("valid-token-123", "Password!")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors[?(@.field == 'newPassword')]").exists());

		verify(authService, never()).resetPassword(requestCaptor.capture());
	}

	@Test
	void resetPassword_WhenPasswordMissingSpecialCharacter_ReturnsBadRequestWithFieldError() throws Exception {
		ArgumentCaptor<ResetPasswordRequest> requestCaptor = ArgumentCaptor.forClass(ResetPasswordRequest.class);

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("valid-token-123", "Password123")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors[?(@.field == 'newPassword')]").exists());

		verify(authService, never()).resetPassword(requestCaptor.capture());
	}

	@Test
	void resetPassword_WhenServiceThrowsUnexpectedException_ReturnsInternalServerError() throws Exception {
		doThrow(new RuntimeException("Database unavailable"))
				.when(authService).resetPassword(any(ResetPasswordRequest.class));

		mockMvc.perform(post("/api/auth/reset-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(resetPasswordRequestJson("valid-token-123", "NewP@ssw0rd")))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.error").value("Internal Server Error"))
				.andExpect(jsonPath("$.message").value("Database unavailable"));
	}

	private String forgotPasswordRequestJson(String email) {
		return """
				{
				  "email": "%s"
				}
				""".formatted(email);
	}

	private String resetPasswordRequestJson(String token, String newPassword) {
		return """
				{
				  "token": "%s",
				  "newPassword": "%s"
				}
				""".formatted(token, newPassword);
	}
}
