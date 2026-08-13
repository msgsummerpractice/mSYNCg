package com.example.demo;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.exceptions.ValidationException;
import com.example.demo.model.Location;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.dao.DataAccessResourceFailureException;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Test
	void createUserWhenEmailAlreadyExistsThrowsValidationException() {
		UserRequest request = buildValidRequest();
		when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

		ValidationException ex = assertThrows(ValidationException.class, () -> userService.createUser(request));

		assertEquals("email", ex.getField());
		assertEquals("Email address is already in use.", ex.getReason());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void createUserWhenPasswordIsNullSavesUserWithNullEncodedPassword() throws Exception {
		UserRequest request = buildValidRequest();
		request.setPassword(null);
		User mappedUser = new User();
		UserResponse mappedResponse = new UserResponse();

		when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
		when(modelMapper.map(request, User.class)).thenReturn(mappedUser);
		when(passwordEncoder.encode(null)).thenReturn(null);
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(modelMapper.map(mappedUser, UserResponse.class)).thenReturn(mappedResponse);

		UserResponse response = userService.createUser(request);

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());
		User savedUser = userCaptor.getValue();

		assertNotNull(response);
		assertNull(readPrivateField(savedUser, "password"));
		assertEquals(Boolean.TRUE, readPrivateField(savedUser, "status"));
		assertEquals(UserRole.PARTICIPANT, readPrivateField(savedUser, "role"));
	}

	@Test
	void createUserWhenRepositorySaveFailsPropagatesException() {
		UserRequest request = buildValidRequest();
		User mappedUser = new User();
		when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
		when(modelMapper.map(request, User.class)).thenReturn(mappedUser);
		when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
		when(userRepository.save(any(User.class)))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		assertThrows(DataAccessResourceFailureException.class, () -> userService.createUser(request));
	}

	@Test
	void createUserWhenValidInputEncodesPasswordAndSetsDefaults() throws Exception {
		UserRequest request = buildValidRequest();
		User mappedUser = new User();
		UserResponse mappedResponse = new UserResponse();
		when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
		when(modelMapper.map(request, User.class)).thenReturn(mappedUser);
		when(passwordEncoder.encode(request.getPassword())).thenReturn("$2encodedPassword");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(modelMapper.map(mappedUser, UserResponse.class)).thenReturn(mappedResponse);

		UserResponse response = userService.createUser(request);

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());
		User savedUser = userCaptor.getValue();

		String encodedPassword = (String) readPrivateField(savedUser, "password");
		Boolean status = (Boolean) readPrivateField(savedUser, "status");
		UserRole role = (UserRole) readPrivateField(savedUser, "role");

		assertNotNull(response);
		assertNotEquals(request.getPassword(), encodedPassword);
		assertTrue(encodedPassword.startsWith("$2"));
		assertEquals(Boolean.TRUE, status);
		assertEquals(UserRole.PARTICIPANT, role);
		verify(modelMapper).map(request, User.class);
		verify(modelMapper).map(mappedUser, UserResponse.class);
	}

	private UserRequest buildValidRequest() {
		UserRequest request = new UserRequest();
		request.setFirstName("Ada");
		request.setLastName("Lovelace");
		request.setEmail("ada@example.com");
		request.setPassword("StrongP@ssw0rd");
		request.setLocation(Location.CLUJ_NAPOCA);
		return request;
	}

	private Object readPrivateField(Object target, String fieldName) throws Exception {
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(target);
	}
}
