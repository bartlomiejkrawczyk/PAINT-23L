package org.example.authorization.service;

import org.springframework.security.oauth2.core.OAuth2Token;
import reactor.core.publisher.Mono;

public interface JwtTokenService {
    Mono<OAuth2Token> generateToken(long userId);
}
