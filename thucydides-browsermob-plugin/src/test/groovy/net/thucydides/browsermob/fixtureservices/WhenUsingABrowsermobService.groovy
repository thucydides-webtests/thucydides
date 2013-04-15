package net.thucydides.browsermob.fixtureservices

import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.remote.DesiredCapabilities
import spock.lang.Specification

class WhenUsingABrowsermobService extends Specification {

    BrowserMobFixtureService service

    def capabilities = Mock(DesiredCapabilities)
    def environmentVariables = new MockEnvironmentVariables()

    def "should start a browsermob server"() {
        given:
            service = new BrowserMobFixtureService(environmentVariables)

        when:
            service.setup()

        then:
            service.proxyServer
    }

    def "should shut down a browsermob server"() {
        given:
            service = new BrowserMobFixtureService(environmentVariables)

        when:
            service.setup()
            service.shutdown()

        then:
            !service.proxyServer
    }

    def "should only activate browsermob for requested browsers if specified"() {
        given:
            environmentVariables.setProperty("browser.mob.filter","iexplorer")
            environmentVariables.setProperty("webdriver.driver","firefox")
        and:
            service = new BrowserMobFixtureService(environmentVariables)
        when:
            service.setup()

        then:
            !service.proxyServer
    }

    def "should activate browsermob for requested browsers if specified"() {
        given:
            environmentVariables.setProperty("browser.mob.filter","iexplorer")
            environmentVariables.setProperty("webdriver.driver","iexplorer")
        and:
            service = new BrowserMobFixtureService(environmentVariables)
        when:
            service.setup()

        then:
            service.proxyServer
    }

    def "should activate browsermob if no driver is specified"() {
        given:
            environmentVariables.setProperty("browser.mob.filter","iexplorer")
        and:
            service = new BrowserMobFixtureService(environmentVariables)
        when:
            service.setup()

        then:
            service.proxyServer
    }

    def "should configure capabilities with the browser mob proxy config"() {
        given:
            service = new BrowserMobFixtureService(environmentVariables)
            service.setup()
        when:
            service.addCapabilitiesTo(capabilities)
        then:
            1 * capabilities.setCapability('proxy', _)
    }

    def cleanup() {
        if (service) {
            service.shutdown()
        }
    }

}
