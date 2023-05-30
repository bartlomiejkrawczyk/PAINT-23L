package org.example.authorization.controller;

import lombok.RequiredArgsConstructor;
import org.example.authorization.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("anonymous")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AnonymousController {
    private static final int ANONYMOUS_ID = 1;
    private final JwtTokenService jwtTokenService;

    @GetMapping
    public Mono<OAuth2Token> getAnonymousToken() {
        return jwtTokenService.generateToken(ANONYMOUS_ID);
    }
}

