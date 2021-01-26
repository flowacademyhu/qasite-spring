package hu.flowacademy.qasitespring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * ValidationException will be an inheritance of RuntimeException
 * so we don't have to mark it in method declaration or catch it
 *
 * The instance of ValidationException will generates a response
 * with HTTP Status 400 - Bad Request
 */
public class ValidationException extends ResponseStatusException {
    public ValidationException(String effectedField) {
        super(HttpStatus.BAD_REQUEST, effectedField + " was invalid!");
    }
}
