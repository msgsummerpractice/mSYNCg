package com.example.demo.service;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.dto.response.UserViewResponse;
import com.example.demo.exceptions.ValidationException;
import com.example.demo.filtering.users.UserSpec;
import com.example.demo.model.Location;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.dao.DataAccessResourceFailureException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
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

	@Test
	void getUsersWhenUsersExistReturnsMappedPage() {
		User user = new User();
		UserViewResponse viewResponse = new UserViewResponse();
		viewResponse.setId(1);
		viewResponse.setEmail("ada@example.com");
		UserSpec spec = mock(UserSpec.class);
		Pageable pageable = PageRequest.of(0, 20);
		Page<User> usersPage = new PageImpl<>(List.of(user), pageable, 1);

		when(userRepository.findAll(spec, pageable)).thenReturn(usersPage);
		when(modelMapper.map(user, UserViewResponse.class)).thenReturn(viewResponse);

		Page<UserViewResponse> result = userService.getUsers(spec, pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals(viewResponse, result.getContent().get(0));
		assertEquals(pageable, result.getPageable());
		verify(modelMapper).map(user, UserViewResponse.class);
	}

	@Test
	void getUsersWhenNoUsersMatchReturnsEmptyPageWithoutMapping() {
		UserSpec spec = mock(UserSpec.class);
		Pageable pageable = PageRequest.of(0, 20);

		when(userRepository.findAll(spec, pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

		Page<UserViewResponse> result = userService.getUsers(spec, pageable);

		assertTrue(result.getContent().isEmpty());
		assertEquals(0, result.getTotalElements());
		verify(modelMapper, never()).map(any(User.class), eq(UserViewResponse.class));
	}

	@Test
	void getUsersPassesUserSpecAndPageableToRepository() {
		UserSpec spec = mock(UserSpec.class);
		Pageable pageable = PageRequest.of(2, 5);

		when(userRepository.findAll(spec, pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

		userService.getUsers(spec, pageable);

		ArgumentCaptor<UserSpec> specCaptor = ArgumentCaptor.forClass(UserSpec.class);
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(userRepository).findAll(specCaptor.capture(), pageableCaptor.capture());

		assertEquals(spec, specCaptor.getValue());
		assertEquals(pageable, pageableCaptor.getValue());
	}

	@Test
	void getUsersWhenSpecIsNullQueriesRepositoryWithoutFilters() {
		Pageable pageable = PageRequest.of(0, 20);

		when(userRepository.findAll((UserSpec) isNull(), eq(pageable)))
				.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		Page<UserViewResponse> result = userService.getUsers(null, pageable);

		assertTrue(result.getContent().isEmpty());
		verify(userRepository).findAll((UserSpec) isNull(), eq(pageable));
	}

	@Test
	void getUsersWhenRepositoryFailsPropagatesException() {
		UserSpec spec = mock(UserSpec.class);
		Pageable pageable = PageRequest.of(0, 20);

		when(userRepository.findAll(spec, pageable))
				.thenThrow(new DataAccessResourceFailureException("Database unavailable"));

		assertThrows(DataAccessResourceFailureException.class, () -> userService.getUsers(spec, pageable));
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

	@Test
void updateUserRoleWhenUserExistsUpdatesRoleAndReturnsResponse() {
    User user = new User();
    user.setId(1);
    user.setRole(UserRole.PARTICIPANT);

    UserResponse mappedResponse = new UserResponse();

    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    when(modelMapper.map(user, UserResponse.class)).thenReturn(mappedResponse);

    UserResponse response = userService.updateUserRole(1, UserRole.ADMIN);

    assertNotNull(response);
    assertEquals(UserRole.ADMIN, user.getRole());

    verify(userRepository).findById(1);
    verify(userRepository).save(user);
    verify(modelMapper).map(user, UserResponse.class);
}

@Test
void updateUserRoleWhenUserDoesNotExistThrowsException() {
    when(userRepository.findById(99)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> userService.updateUserRole(99, UserRole.ADMIN)
    );

    assertEquals("User not found", exception.getMessage());

    verify(userRepository).findById(99);
    verify(userRepository, never()).save(any(User.class));
    verify(modelMapper, never()).map(any(User.class), eq(UserResponse.class));
}

@Test
void updateUserStatusWhenUserExistsUpdatesStatusAndReturnsResponse() {
    User user = new User();
    user.setId(1);
    user.setStatus(true);

    UserResponse mappedResponse = new UserResponse();

    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    when(modelMapper.map(user, UserResponse.class)).thenReturn(mappedResponse);

    UserResponse response = userService.updateUserStatus(1, false);

    assertNotNull(response);
    assertEquals(Boolean.FALSE, user.getStatus());

    verify(userRepository).findById(1);
    verify(userRepository).save(user);
    verify(modelMapper).map(user, UserResponse.class);
}

@Test
void updateUserStatusWhenUserDoesNotExistThrowsException() {
	when(userRepository.findById(99)).thenReturn(Optional.empty());

	RuntimeException exception = assertThrows(
			RuntimeException.class,
			() -> userService.updateUserStatus(99, false)
	);

	assertEquals("User not found", exception.getMessage());

	verify(userRepository).findById(99);
	verify(userRepository, never()).save(any(User.class));
	verify(modelMapper, never()).map(any(User.class), eq(UserResponse.class));
}


}
