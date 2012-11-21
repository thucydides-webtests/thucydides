package net.thucydides.core.bootstrap

import spock.lang.Specification
import net.thucydides.core.steps.Listeners
import net.thucydides.core.steps.StepEventBus

class WhenConfiguringAThucydidesContext extends Specification {

    def "a context should have a base listener by default"() {
        when:
            def context = ThucydidesContext.newContext()
        then:
            StepEventBus.eventBus.baseStepListener == context.stepListener
    }

    def "a context can be configured with additional listeners"() {
        when:
            def context = ThucydidesContext.newContext(Listeners.loggingListener)
        then:
            StepEventBus.eventBus.registeredListeners.contains Listeners.loggingListener
    }

    def "a context should "() {
        TODO
    }
}
