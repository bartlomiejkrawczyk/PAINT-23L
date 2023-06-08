package org.example.session.service.implementation;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.example.session.entity.GuessEntity;
import org.example.session.entity.SessionEntity;
import org.example.session.entity.WordEntity;
import org.example.session.exception.GuessLimitExceeded;
import org.example.session.exception.IncorrectWordLength;
import org.example.session.exception.NoMatchingWordsFound;
import org.example.session.exception.NonExistentWord;
import org.example.session.exception.SessionDoesNotExist;
import org.example.session.exception.UsersNotMatching;
import org.example.session.exception.WordContainsNonAlphaCharacters;
import org.example.session.mapper.SessionMapper;
import org.example.session.model.FinalResult;
import org.example.session.model.LetterResult;
import org.example.session.model.Session;
import org.example.session.model.WordleResult;
import org.example.session.repository.GuessRepository;
import org.example.session.repository.SessionRepository;
import org.example.session.repository.WordRepository;
import org.example.session.service.WordleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WordleServiceImpl implements WordleService {

	private static final char ALREADY_USED = '#';
	private static final int MAX_GUESSES = 6;

	private final WordRepository wordRepository;
	private final SessionRepository sessionRepository;
	private final GuessRepository guessRepository;

	private final SessionMapper sessionMapper;

	private final Random random;

	@Override
	public Mono<Session> initializeSession(int userId, int languageId, int wordLength) {
		return getRandomWord(languageId, wordLength)
				.map(word ->
						SessionEntity.builder()
								.userId(userId)
								.languageId(languageId)
								.wordLength(wordLength)
								.wordNumber(word.getNumber())
								.createdDate(Instant.now())
								.build()
				)
				.flatMap(sessionRepository::save)
				.map(sessionMapper::entityToDto);
	}

	private Mono<WordEntity> getRandomWord(int languageId, int wordLength) {
		return wordRepository.countAllByLanguageIdAndLength(languageId, wordLength)
				.filter(it -> it > 0)
				.map(random::nextInt)
				.map(it -> it + 1)
				.flatMap(randomNumber -> wordRepository.findByLanguageIdAndLengthAndNumber(languageId, wordLength, randomNumber))
				.switchIfEmpty(Mono.error(NoMatchingWordsFound::new));
	}

	@Override
	@Transactional
	public Mono<WordleResult> handleGuess(int userId, long sessionId, String guess) {
		return findSession(sessionId, userId)
				.flatMap(session -> handleSessionGuess(session, guess));
	}

	@Override
	public Mono<FinalResult> retrieveResult(int userId, long sessionId) {
		return findSession(sessionId, userId)
				.flatMap(this::findCorrectWord)
				.map(WordEntity::getWord)
				.map(FinalResult::new);
	}

	private Mono<WordleResult> handleSessionGuess(SessionEntity session, String guess) {
		return checkIfWordExists(session, guess)
				.flatMap(guessWord ->
						saveGuess(session, guess).map(ignore -> guessWord)
				)
				.flatMap(guessWord -> calculateResult(session, guessWord));
	}

	private Mono<WordleResult> calculateResult(SessionEntity session, WordEntity guessWord) {
		return findCorrectWord(session)
				.map(correctWord -> Pair.of(guessWord, correctWord))
				.flatMap(words -> getLetterResults(words.getLeft(), words.getRight()))
				.map(wordResult ->
						WordleResult.builder()
								.currentGuess(wordResult)
								.build()
				);
	}

	private Mono<SessionEntity> findSession(long sessionId, int userId) {
		return sessionRepository.findById(sessionId)
				.switchIfEmpty(Mono.error(SessionDoesNotExist::new))
				.filter(session -> session.getUserId() == userId)
				.switchIfEmpty(Mono.error(UsersNotMatching::new));
	}

	private Mono<WordEntity> checkIfWordExists(SessionEntity session, String guess) {
		if (StringUtils.length(guess) != session.getWordLength()) {
			return Mono.error(IncorrectWordLength::new);
		}
		if (!StringUtils.isAlpha(guess)) {
			return Mono.error(WordContainsNonAlphaCharacters::new);
		}
		return wordRepository.findByLanguageIdAndLengthAndWord(session.getLanguageId(), session.getWordLength(), guess)
				.switchIfEmpty(Mono.error(NonExistentWord::new));
	}

	private Mono<GuessEntity> saveGuess(SessionEntity session, String guess) {
		return guessRepository.countAllBySessionId(session.getId())
				.filter(number -> number < MAX_GUESSES)
				.switchIfEmpty(Mono.error(GuessLimitExceeded::new))
				.map(number -> GuessEntity.builder()
						.sessionId(session.getId())
						.guess(guess)
						.guessNumber(number)
						.createdDate(Instant.now())
						.build())
				.flatMap(guessRepository::save);
	}

	private Mono<WordEntity> findCorrectWord(SessionEntity session) {
		return wordRepository.findByLanguageIdAndLengthAndNumber(session.getLanguageId(), session.getWordLength(), session.getWordNumber());
	}

	private Mono<List<LetterResult>> getLetterResults(WordEntity guessWord, WordEntity correctWord) {
		var guess = guessWord.getWord().chars().toArray();
		var correct = correctWord.getWord().chars().toArray();
		var result = new LetterResult[correct.length];
		Arrays.fill(result, LetterResult.LETTER_ABSENT);

		for (var i = 0; i < correct.length; i++) {
			if (guess[i] == correct[i]) {
				result[i] = LetterResult.CORRECT_LETTER_CORRECT_PLACE;
				correct[i] = ALREADY_USED;
			}
		}

		for (var i = 0; i < correct.length; i++) {
			if (result[i] == LetterResult.CORRECT_LETTER_CORRECT_PLACE) {
				continue;
			}

			for (var j = 0; j < correct.length; j++) {
				if (guess[i] == correct[j]) {
					result[i] = LetterResult.CORRECT_LETTER_INCORRECT_PLACE;
					correct[j] = ALREADY_USED;
				}
			}
		}

		return Mono.just(Arrays.asList(result));
	}
}
