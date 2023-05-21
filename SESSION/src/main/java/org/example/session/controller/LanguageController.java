package org.example.session.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.session.model.Language;
import org.example.session.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("language")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LanguageController {

	private final LanguageService service;

	@GetMapping
	@Operation(security = @SecurityRequirement(name = "bearer"))
	public Flux<Language> getAvailableLanguages() {
		return service.getAllLanguages();
	}
}
