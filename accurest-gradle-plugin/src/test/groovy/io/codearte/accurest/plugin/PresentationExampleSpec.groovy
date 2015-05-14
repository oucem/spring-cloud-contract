package io.codearte.accurest.plugin

import nebula.test.IntegrationSpec
import spock.lang.Stepwise

@Stepwise
class PresentationExampleSpec extends IntegrationSpec {

	void setup() {
		copyResources("functionalTest/presentationExample", "")
		runTasksSuccessfully('clean')   //delete accidental output when previously importing SimpleBoot into Idea to tweak it
	}

	def "should pass basic flow"() {
		given:
			assert fileExists('build.gradle')
		expect:
			runTasksSuccessfully('check')
	}

}
