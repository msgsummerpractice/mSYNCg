package com.example.demo.controller;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.dto.response.UserViewResponse;
import com.example.demo.exceptions.GlobalExceptionHandler;
import com.example.demo.exceptions.ValidationException;
import com.example.demo.filtering.users.UserSpec;
import com.example.demo.model.Location;
import com.example.demo.model.UserRole;
import com.example.demo.service.UserService;
import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	private MockMvc mockMvc;

	private static final String CLUJ_NAPOCA_JSON = Location.CLUJ_NAPOCA.getDisplayValue();

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(userController)
				.setCustomArgumentResolvers(
						new SpecificationArgumentResolver(),
						new PageableHandlerMethodArgumentResolver())
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}

	@Test
	void createUser_whenRequestIsValid_returnsOkWithUserResponse() throws Exception {
		UserResponse response = new UserResponse(1, "Ada", "Lovelace", "ada@example.com",
				Location.CLUJ_NAPOCA.name(), true, null, UserRole.PARTICIPANT.name());

		when(userService.create(any(UserRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validRequestJson()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.firstName").value("Ada"))
				.andExpect(jsonPath("$.email").value("ada@example.com"))
				.andExpect(jsonPath("$.role").value(UserRole.PARTICIPANT.name()));

		verify(userService).create(any(UserRequest.class));
	}

	@Test
	void createUser_whenEmailIsInvalid_returnsBadRequestWithFieldError() throws Exception {
		String body = requestJson("Ada", "Lovelace", "not-an-email", "StrongP@ssw0rd", CLUJ_NAPOCA_JSON);

		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.fieldErrors[0].field").value("email"));

		verify(userService, never()).create(any(UserRequest.class));
	}

	@Test
	void createUser_whenPasswordIsWeak_returnsBadRequest() throws Exception {
		String body = requestJson("Ada", "Lovelace", "ada@example.com", "weak", CLUJ_NAPOCA_JSON);

		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fieldErrors[0].field").value("password"));

		verify(userService, never()).create(any(UserRequest.class));
	}

	@Test
	void createUser_whenRequiredFieldsAreMissing_returnsBadRequest() throws Exception {
		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Request validation failed"))
				.andExpect(jsonPath("$.fieldErrors.length()").value(5));

		verify(userService, never()).create(any(UserRequest.class));
	}

	@Test
	void createUser_whenServiceThrowsValidationException_returnsBadRequest() throws Exception {
		when(userService.create(any(UserRequest.class)))
				.thenThrow(new ValidationException("email", "Email address is already in use."));

		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validRequestJson()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fieldErrors[0].field").value("email"))
				.andExpect(jsonPath("$.fieldErrors[0].reason").value("Email address is already in use."));
	}

	@Test
	void createUser_whenServiceThrowsUnexpectedException_returnsInternalServerError() throws Exception {
		when(userService.create(any(UserRequest.class)))
				.thenThrow(new RuntimeException("Database unavailable"));

		mockMvc.perform(post("/api/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validRequestJson()))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.error").value("Internal Server Error"))
				.andExpect(jsonPath("$.message").value("Database unavailable"));
	}

	@Test
	void getUsers_whenNoFilters_returnsPageOfUsers() throws Exception {
		Page<UserViewResponse> page = new PageImpl<>(List.of(buildViewResponse()), PageRequest.of(0, 20), 1);

		when(userService.getAll(any(UserSpec.class), any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/api/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(1))
				.andExpect(jsonPath("$.content[0].email").value("ada@example.com"))
				.andExpect(jsonPath("$.content[0].role").value(UserRole.PARTICIPANT.getDisplayValue()))
				.andExpect(jsonPath("$.totalElements").value(1));
	}

	@Test
	void getUsers_whenNoResults_returnsEmptyPage() throws Exception {
		when(userService.getAll(any(UserSpec.class), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

		mockMvc.perform(get("/api/users").param("firstName", "Nobody"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isEmpty())
				.andExpect(jsonPath("$.totalElements").value(0));
	}

	@Test
	void getUsers_whenPaginationParamsProvided_forwardsPageableToService() throws Exception {
		when(userService.getAll(any(UserSpec.class), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(), PageRequest.of(2, 5), 0));

		mockMvc.perform(get("/api/users")
				.param("page", "2")
				.param("size", "5"))
				.andExpect(status().isOk());

		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(userService).getAll(any(UserSpec.class), pageableCaptor.capture());

		assertEquals(2, pageableCaptor.getValue().getPageNumber());
		assertEquals(5, pageableCaptor.getValue().getPageSize());
	}

	@Test
	void getUsers_whenFiltersProvided_resolvesUserSpec() throws Exception {
		Page<UserViewResponse> page = new PageImpl<>(List.of(buildViewResponse()), PageRequest.of(0, 20), 1);

		when(userService.getAll(any(UserSpec.class), any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/api/users")
				.param("firstName", "Ada")
				.param("role", "PARTICIPANT")
				.param("status", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].firstName").value("Ada"));

		ArgumentCaptor<UserSpec> specCaptor = ArgumentCaptor.forClass(UserSpec.class);
		verify(userService).getAll(specCaptor.capture(), any(Pageable.class));

		assertNotNull(specCaptor.getValue());
	}

	@Test
	void getUsers_whenServiceThrowsUnexpectedException_returnsInternalServerError() throws Exception {
		when(userService.getAll(any(UserSpec.class), any(Pageable.class)))
				.thenThrow(new RuntimeException("Database unavailable"));

		mockMvc.perform(get("/api/users"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.error").value("Internal Server Error"));
	}

	private String validRequestJson() {
		return requestJson("Ada", "Lovelace", "ada@example.com", "StrongP@ssw0rd", CLUJ_NAPOCA_JSON);
	}

	private String requestJson(String firstName, String lastName, String email, String password, String location) {
		return """
				{
				  "firstName": "%s",
				  "lastName": "%s",
				  "email": "%s",
				  "password": "%s",
				  "location": "%s"
				}
				""".formatted(firstName, lastName, email, password, location);
	}

	private UserViewResponse buildViewResponse() {
		return new UserViewResponse(1, "Ada", "Lovelace", "ada@example.com",
				UserRole.PARTICIPANT, Location.CLUJ_NAPOCA, true);
	}
}
