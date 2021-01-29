package hu.flowacademy.qasitespring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoContentException extends ResponseStatusException {
    public NoContentException(String param) {
        super(HttpStatus.NO_CONTENT, "Value not found: " + param);
    }
}
