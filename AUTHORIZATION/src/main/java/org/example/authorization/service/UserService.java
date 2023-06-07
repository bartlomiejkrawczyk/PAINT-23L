package org.example.authorization.service;

import org.example.authorization.model.LoginRequest;
import org.example.authorization.model.RegisterRequest;
import org.example.authorization.model.User;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> login(LoginRequest request);
    Mono<Boolean> register(RegisterRequest request);
}
