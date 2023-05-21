//package org.example.session.controller
//
//import org.example.session.entity.SessionEntity
//import org.example.session.entity.WordEntity
//import org.example.session.model.LetterResult
//import org.example.session.model.Session
//import org.example.session.model.WordleResult
//import org.example.session.repository.SessionRepository
//import org.example.session.repository.WordRepository
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers
//import spock.lang.Specification
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//class WordleControllerIntegrationTest extends Specification {
//
//	@Autowired
//	MockMvc mvc
//
//	@Autowired
//	WordRepository wordRepository
//
//	@Autowired
//	SessionRepository sessionRepository
//
//	def 'cleanup'() {
//		wordRepository.deleteAll().block()
//		sessionRepository.deleteAll().block()
//	}
//
//	def insertWord(String word) {
//		var entity = WordEntity.builder()
//				.languageId(1)
//				.length(word.length())
//				.number(1)
//				.word(word)
//				.build()
//		wordRepository.save(entity).block()
//	}
//
//	long insertSession(String word) {
//		var entity = SessionEntity.builder()
//				.userId(1)
//				.languageId(1)
//				.wordLength(word.length())
//				.wordNumber(1)
//				.build()
//		return sessionRepository.save(entity).block().getId()
//	}
//
//	def 'Should initialize session correctly'() {
//		given:
//		insertWord("plane")
//
//		expect:
//		var response = (Session) mvc.perform(
//				MockMvcRequestBuilders.get("/wordle?languageId=1")
//						.header("wordLength", 5)
//		)
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn()
//				.asyncResult
//
//		response.getUserId() == 1
//		response.getWordLength() == 5
//		response.getLanguageId() == 1
//	}
//
//	def 'Should be able to guess immediately'() {
//		given:
//		insertWord(guess)
//		var sessionId = insertSession(guess)
//
//		expect:
//		var response = (WordleResult) mvc.perform(
//				MockMvcRequestBuilders.post("/wordle?languageId=1")
//						.header("sessionId", sessionId)
//						.content(guess)
//		)
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn()
//				.asyncResult
//
//		response.getCurrentGuess()
//
//		where:
//		guess || result
//		"a"   || [LetterResult.CORRECT_LETTER_CORRECT_PLACE]
//		"abc" || [LetterResult.CORRECT_LETTER_CORRECT_PLACE, LetterResult.CORRECT_LETTER_CORRECT_PLACE, LetterResult.CORRECT_LETTER_CORRECT_PLACE]
//	}
//}