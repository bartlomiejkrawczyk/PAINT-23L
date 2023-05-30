package org.example.authorization.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.authorization.model.LoginRequest;
import org.example.authorization.service.JwtTokenService;
import org.example.authorization.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("login")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoginController {
    private final UserService service;
    private final JwtTokenService jwtTokenService;

    @PostMapping
    public Mono<OAuth2Token> login(@Valid @RequestBody LoginRequest loginRequest) {
        return service.login(loginRequest)
                .flatMap(user -> jwtTokenService.generateToken(user.getId()));
    }
}
