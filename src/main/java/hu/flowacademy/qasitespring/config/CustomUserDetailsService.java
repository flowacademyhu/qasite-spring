package hu.flowacademy.qasitespring.config;

import hu.flowacademy.qasitespring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
/**
 * @Transactional has used, because we want to connect to the database
 */
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * loadUserByUsername need to return a UserDetails instance
     * fortunately we implement this interface on our User class
     * so if we can get a user from database (filtered by the username)
     * this object would be fine for Spring Security to work with
     * otherwise, if we can't find a user by username, throwing an UsernameNotFoundException
     * @throws UsernameNotFoundException if user not found by username in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
