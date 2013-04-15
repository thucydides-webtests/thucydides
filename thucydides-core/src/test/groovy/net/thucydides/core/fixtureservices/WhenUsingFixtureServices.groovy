package net.thucydides.browsermob.fixtureservices

import net.thucydides.core.fixtureservices.ClasspathFixtureProviderService
import net.thucydides.core.fixtureservices.FixtureProviderService
import net.thucydides.core.fixtureservices.FixtureService
import net.thucydides.core.fixtureservices.SampleFixtureService
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SupportedWebDriver
import net.thucydides.core.webdriver.WebDriverFactory
import net.thucydides.core.webdriver.WebdriverInstanceFactory
import net.thucydides.core.webdriver.firefox.FirefoxProfileEnhancer
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenUsingFixtureServices extends Specification {
    def "should load fixture services from the classpath"() {
        given:
            def fixtureServiceLoader = new ClasspathFixtureProviderService()
        when:
            def fixtureServices = fixtureServiceLoader.getFixtureServices()
        then:
            fixtureServices.find { it.getClass() == SampleFixtureService }
    }

    def webdriverInstanceFactory = Mock(WebdriverInstanceFactory)
    def environmentVariables = new MockEnvironmentVariables()
    def firefoxProfileEnhancer = Mock(FirefoxProfileEnhancer)
    def fixtureProviderService = Mock(FixtureProviderService)
    def fixtureService = Mock(FixtureService)
    def driver = Mock(WebDriver)

    def "fixture services should be invoked when creating driver instances"() {
        given:
            fixtureProviderService.getFixtureServices() >> [fixtureService]
            webdriverInstanceFactory.newFirefoxDriver(_) >> driver
            def webdriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables,
                                                        firefoxProfileEnhancer,fixtureProviderService)
        when:
            webdriverFactory.newInstanceOf(SupportedWebDriver.FIREFOX)
        then:
            1 * fixtureService.addCapabilitiesTo(_)

    }
}
