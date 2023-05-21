package org.example.session.controller;

import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("anonymous")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AnonymousController {

	private static final int ANONIM_ID = 1;
	private final JwtEncoder jwtEncoder;

	@GetMapping
	public Mono<OAuth2Token> getAnonymousToken() {
		return generateToken();
	}

	private Mono<OAuth2Token> generateToken() {
		var now = Instant.now();
		var expiry = Duration.ofHours(5);

		var claims = JwtClaimsSet.builder()
				.issuedAt(now)
				.expiresAt(now.plus(expiry))
				.subject(String.valueOf(ANONIM_ID))
				.build();

		return Mono.just(claims)
				.map(JwtEncoderParameters::from)
				.mapNotNull(jwtEncoder::encode);
	}
}
