package net.thucydides.core.webdriver

import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Specification

class WhenProvidingYourOwnWebdriverInstance extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def "should be able to use the 'PROVIDED' type to say that we want to provide our own driver"() {
        expect:
            SupportedWebDriver.listOfSupportedDrivers().contains "PROVIDED"
    }

    def "should be able to ask the WebDriverFactory to provide a custom driver"() {

        given:
            environmentVariables.setProperty("webdriver.driver","provided")
            environmentVariables.setProperty("webdriver.provided.type","mydriver")
            environmentVariables.setProperty("webdriver.provided.mydriver","net.thucydides.core.webdriver.MyDriverSource")
            def factory = new WebDriverFactory(environmentVariables)
        when:
            def driver = factory.newWebdriverInstance(ProvidedDriver);
        then:
            driver.class == HtmlUnitDriver
    }
}
