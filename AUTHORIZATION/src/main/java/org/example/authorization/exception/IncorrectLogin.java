package org.example.authorization.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class IncorrectLogin extends RuntimeException {

    public IncorrectLogin() {
        super("Invalid login credentials. Please check your username and password.");
    }
}
