package org.example.authorization.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WeakPassword extends RuntimeException {
    public WeakPassword() {
        super("The password must contain at least one uppercase letter, one lowercase letter, and one digit.");
    }
}
