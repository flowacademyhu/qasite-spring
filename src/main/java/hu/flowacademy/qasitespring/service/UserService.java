package hu.flowacademy.qasitespring.service;

import hu.flowacademy.qasitespring.exception.InvalidRequestBody;
import hu.flowacademy.qasitespring.exception.ValidationException;
import hu.flowacademy.qasitespring.model.Role;
import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    /**
     * Injecting the instance of the UserRepository here
     */
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User save(User user) {
        validate(user);
        user.setId(UUID.randomUUID().toString());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    private void validate(User user) {
        if (user.getId() != null) {
            throw new InvalidRequestBody();
        }
        if (!StringUtils.hasText(user.getUsername())) {
            throw new ValidationException("username");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new ValidationException("password");
        }
        if (!StringUtils.hasText(user.getFullName())) {
            throw new ValidationException("fullName");
        }
    }

}
