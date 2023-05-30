package org.example.authorization.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.authorization.model.RegisterRequest;
import org.example.authorization.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("register")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegisterController {
    private final UserService service;

    @PostMapping
    public Mono<Boolean> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return service.register(registerRequest);
    }
}
