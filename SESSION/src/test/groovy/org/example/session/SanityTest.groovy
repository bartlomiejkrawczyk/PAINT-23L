package org.example.session

import spock.lang.Specification


class SanityTest extends Specification {

	def 'Should add two numbers correctly'() {
		expect:
		first + second == result

		where:
		first | second || result
		1     | 2      || 3
		2     | 2      || 4
		2     | -2     || 0
		-1    | 2      || 1
	}

//	def 'Should start Spring application correctly'() {
//		expect:
//		SessionApplication.main()
//	}
}
