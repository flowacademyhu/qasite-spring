package hu.flowacademy.qasitespring.service;

import hu.flowacademy.qasitespring.exception.InvalidRequestBody;
import hu.flowacademy.qasitespring.exception.ValidationException;
import hu.flowacademy.qasitespring.model.Role;
import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    private static final UUID userId = UUID.randomUUID();

    @Mock
    private UserRepository userRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void whenInvalidUserSavingThenValidateThrowsException() {
        assertThrows(InvalidRequestBody.class, () ->
                userService.save(givenUserWithId()));
        assertThrows(ValidationException.class, () ->
                userService.save(givenUserWithoutUsername()));
        assertThrows(ValidationException.class, () ->
                userService.save(givenUserWithoutPassword()));
        assertThrows(ValidationException.class, () ->
                userService.save(givenUserWithoutFullName()));
    }

    @Test
    public void whenValidUserSavingThenIdAndRoleSet() {
        var user = givenValidUser();
        when(userRepository.save(any())).thenReturn(User.builder()
                .id(userId.toString())
                .username("username")
                .password("njdnawwsncfwaiofnoief")
                .fullName("Full Name")
                .role(Role.USER)
                .build());

        User result = userService.save(user);
        // verify checks if userRepository's save method called
//        verify(userRepository.save(any()));

        assertEquals(user.getFullName(), result.getFullName());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(Role.USER, result.getRole());
        assertEquals(userId.toString(), result.getId());
        assertNotEquals("", result.getPassword());
        assertNotNull(result.getPassword());
    }

    private User givenValidUser() {
        return User.builder()
                .username("username")
                .password("password")
                .fullName("Full Name")
                .build();
    }

    private User givenUserWithId() {
        return User.builder().id(UUID.randomUUID().toString()).build();
    }

    private User givenUserWithoutUsername() {
        return User.builder()
                .fullName("Full Name")
                .password("password")
                .build();
    }

    private User givenUserWithoutPassword() {
        return User.builder()
                .username("username")
                .fullName("Full Name")
                .build();
    }

    private User givenUserWithoutFullName() {
        return User.builder()
                .username("username")
                .password("password")
                .build();
    }

}