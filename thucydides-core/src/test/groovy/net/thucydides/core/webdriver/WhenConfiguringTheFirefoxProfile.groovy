package net.thucydides.core.webdriver

import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.firefox.FirefoxProfile
import spock.lang.Specification

class WhenConfiguringTheFirefoxProfile extends Specification {

    def environmentVariables = new MockEnvironmentVariables()
    def webdriverInstanceFactory = Mock(WebdriverInstanceFactory)
    WebDriverFactory webDriverFactory

    def setup() {
        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables)
    }

    def "a firefox instance will not assume untrusted certificates if specifically requested not to"() {
        when: "we don't want firefox to assume untrusted certificates"
            environmentVariables.setProperty(SystemPropertiesConfiguration.ASSUME_UNTRUSTED_CERTIFICATE_ISSUER, "false");
            def profile = webDriverFactory.buildFirefoxProfile()
        then: "the firefox profile will not assume them"
            profile.acceptUntrustedCerts
    }


    def "a firefox instance will assume untrusted certificates by default"() {
        when:
            FirefoxProfile profile = webDriverFactory.buildFirefoxProfile()
        then:
            profile.untrustedCertIssuer
    }
}
