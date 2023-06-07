package org.example.authorization.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.authorization.model.User;
import org.example.authorization.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JwtTokenServiceImpl implements JwtTokenService {
    private final JwtEncoder jwtEncoder;

    public Mono<OAuth2Token> generateToken(long userId) {
        var now = Instant.now();
        var expiry = Duration.ofHours(5);

        var claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plus(expiry))
                .subject(String.valueOf(userId))
                .build();

        return Mono.just(claims)
                .map(JwtEncoderParameters::from)
                .mapNotNull(jwtEncoder::encode);
    }
}
