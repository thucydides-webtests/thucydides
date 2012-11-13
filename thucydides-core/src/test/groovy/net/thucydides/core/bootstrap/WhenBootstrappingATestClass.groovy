package net.thucydides.core.bootstrap

import spock.lang.Specification
import net.thucydides.core.annotations.Step
import net.thucydides.core.annotations.Steps
import net.thucydides.core.bootstrap.ThucydidesBootstrap

import static net.thucydides.core.bootstrap.ThucydidesBootstrap.*

/**
 * How to bootstrap a test class for use with Thucydides.
 * This is designed to be an API that can be used for more standardized integration with other test libraries.
 */
class WhenBootstrappingATestClass extends Specification {

    static class StepLibrary {
        @Step
        def step1() {}
    }

    static class SampleTestClass {
        @Steps
        StepLibrary steps
    }

    def "should instantiate classes annotated with the @Step annotation"() {
        given: "a test class object with a field annotated with @Step"
            def testCase = new SampleTestClass()
        when: "we bootstrap the object"
            ThucydidesBootstrap.initialize(testCase)
        then: "the step field should have been instantiated"
            testCase.steps != null
    }
}
