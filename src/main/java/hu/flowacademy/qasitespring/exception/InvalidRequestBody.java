package hu.flowacademy.qasitespring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * InvalidRequestBody will be an inheritance of RuntimeException
 * so we don't have to mark it in method declaration or catch it
 *
 * The instance of InvalidRequestBody will generates a response
 * with HTTP Status 400 - Bad Request
 */
public class InvalidRequestBody extends ResponseStatusException {
    public InvalidRequestBody() {
        super(HttpStatus.BAD_REQUEST);
    }
}
