package org.example.authorization.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PasswordsDoNotMatch extends RuntimeException {
    public PasswordsDoNotMatch() {
        super("Passwords do not match.");
    }
}
