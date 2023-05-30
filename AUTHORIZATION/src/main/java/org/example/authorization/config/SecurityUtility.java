package org.example.authorization.config;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

@UtilityClass
public class SecurityUtility {

    public Mono<Integer> retrieveId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .mapNotNull(Authentication::getPrincipal)
                .<String>handle((it, sink) -> {
                    if (it instanceof Jwt jwt) {
                        sink.next(jwt.getSubject());
                    } else {
                        sink.error(new RuntimeException());
                    }
                })
                .map(Integer::parseInt);
    }
}
