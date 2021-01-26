package hu.flowacademy.qasitespring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadCredentials extends ResponseStatusException {
    public BadCredentials() {
        super(HttpStatus.FORBIDDEN, "Invalid authentication request format");
    }
}
