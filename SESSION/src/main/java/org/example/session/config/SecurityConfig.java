package org.example.session.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.web.access.server.BearerTokenServerAccessDeniedHandler;
import org.springframework.security.oauth2.server.resource.web.server.BearerTokenServerAuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

	@Value("${jwt.private.key}")
	private RSAPrivateKey privateKey;

	@Value("${jwt.public.key}")
	private RSAPublicKey publicKey;

	@Value("${springdoc.api-docs.path}")
	private String restApiDocPath;

	@Value("${springdoc.swagger-ui.path}")
	private String swaggerPath;

	@Value("${frontend.url}")
	private String frontendUrl;

	@Bean
	public SecurityWebFilterChain securityFilterChain(@Autowired ServerHttpSecurity http) {
		http
				.cors()
				.and()
				.csrf().disable();

		http
				.exceptionHandling(exceptions ->
						exceptions
								.authenticationEntryPoint(new BearerTokenServerAuthenticationEntryPoint())
								.accessDeniedHandler(new BearerTokenServerAccessDeniedHandler()));

		http
				.authorizeExchange()
				.pathMatchers("/").permitAll()
				.pathMatchers(String.format("%s**", restApiDocPath)).permitAll()
				.pathMatchers(String.format("%s/**", restApiDocPath)).permitAll()
				.pathMatchers(swaggerPath).permitAll()
				.pathMatchers("/webjars/swagger-ui/**").permitAll()
				.anyExchange().authenticated()
				.and()
				.oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource createCorsConfigSource() {
		var source = new UrlBasedCorsConfigurationSource();
		var config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin(frontendUrl);
		config.addAllowedMethod("*");
		config.addAllowedHeader("*");

		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public JwtEncoder jwtEncoder() {
		var jsonWebKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.build();
		var jsonWebKeySet = new ImmutableJWKSet<>(new JWKSet(jsonWebKey));
		return new NimbusJwtEncoder(jsonWebKeySet);
	}

	@Bean
	public ReactiveJwtDecoder jwtDecoder() {
		return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
	}

	@Bean
	public JwtReactiveAuthenticationManager jwtAuthenticationManager(@Autowired ReactiveJwtDecoder jwtDecoder) {
		return new JwtReactiveAuthenticationManager(jwtDecoder);
	}
}
