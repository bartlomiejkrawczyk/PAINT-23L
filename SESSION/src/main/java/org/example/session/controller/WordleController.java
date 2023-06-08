package org.example.session.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.session.config.SecurityUtility;
import org.example.session.model.FinalResult;
import org.example.session.model.GuessWrapper;
import org.example.session.model.Session;
import org.example.session.model.WordleResult;
import org.example.session.service.WordleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("wordle")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WordleController {

	private final WordleService service;

	@GetMapping
	@Operation(security = @SecurityRequirement(name = "bearer"))
	public Mono<Session> initializeSession(
			@RequestParam Integer languageId,
			@RequestHeader(required = false, defaultValue = "5") Integer wordLength
	) {
		return SecurityUtility.retrieveId()
				.flatMap(userId -> service.initializeSession(userId, languageId, wordLength));
	}

	@PostMapping
	@Operation(security = @SecurityRequirement(name = "bearer"))
	public Mono<WordleResult> guess(
			@RequestHeader Long sessionId,
			@Valid @RequestBody GuessWrapper guess
	) {
		return SecurityUtility.retrieveId()
				.flatMap(userId -> service.handleGuess(userId, sessionId, guess.getGuess()));
	}

	@GetMapping("/result")
	public Mono<FinalResult> retrieveWord(
			@RequestHeader Long sessionId
	) {
		return SecurityUtility.retrieveId()
				.flatMap(userId -> service.retrieveResult(userId, sessionId));
	}
}
