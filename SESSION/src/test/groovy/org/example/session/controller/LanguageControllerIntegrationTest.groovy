//package org.example.session.controller
//
//import org.example.session.entity.LanguageEntity
//import org.example.session.model.Language
//import org.example.session.repository.LanguageRepository
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
//class LanguageControllerIntegrationTest extends Specification {
//
//	@Autowired
//	MockMvc mvc
//
//	@Autowired
//	LanguageRepository repository
//
//	def 'cleanup'() {
//		repository.deleteAll().block()
//	}
//
//	def 'Should retrieve all the languages correctly'() {
//		given:
//		var saved = repository.save(entity).block()
//
//		expect:
//		var response = (List<Language>) mvc.perform(MockMvcRequestBuilders.get("/language"))
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn()
//				.asyncResult
//
//		for (def dto : response) {
//			dto.id == saved.id
//			dto.code == saved.code
//			dto.name == saved.name
//			dto.flagUrl == saved.flagUrl
//			println(dto)
//		}
//
//		response.size() == 1
//
//		where:
//		entity << [
//				LanguageEntity.builder().code('PL').name('Polski').flagUrl('shorturl.at/rvHLO').build(),
//				LanguageEntity.builder().code('GB').name('Great Britain').flagUrl('shorturl.at/hKMY7').build()
//		]
//	}
//}
