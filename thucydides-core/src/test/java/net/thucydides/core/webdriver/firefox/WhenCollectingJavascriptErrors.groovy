package net.thucydides.core.webdriver.firefox

import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import spock.lang.Specification

class WhenCollectingJavascriptErrors extends Specification {


    def firefoxWebdriver = Mock(FirefoxDriver)
    def otherWebdriver = Mock(WebDriver)

    def error1 = [errorMessage:"Oh crap", sourceName:"dodgy.js", lineNumber:100]
    def error2 = [errorMessage:"Oh nose!", sourceName:"dodgy.js", lineNumber:100]

    def "should collect javascript errors raised by Firefox"() {
        given:
            firefoxWebdriver.executeScript(_) >> [error1]
            def errorCollector = new JSErrorCollector(firefoxWebdriver)
        when:
            errorCollector.checkForJavascriptErrors()
        then:
            AssertionError errors = thrown()
            errors.message.contains("Oh crap")
    }

    def "should collect multiple javascript errors"() {
        given:
            firefoxWebdriver.executeScript(_) >> [error1, error2]
            def errorCollector = new JSErrorCollector(firefoxWebdriver)
        when:
            errorCollector.checkForJavascriptErrors()
        then:
            AssertionError errors = thrown()
            errors.message.contains("Oh crap") && errors.message.contains("Oh nose!")
        }

    def "should return no errors if the driver does no support Javascript"() {
        given:
            def errorCollector = new JSErrorCollector(otherWebdriver)
        when:
            errorCollector.checkForJavascriptErrors()
        then:
            notThrown(AssertionError)

    }

    def "should return no errors if the driver does not raise any errors"() {
        given:
            firefoxWebdriver.executeScript(_) >> []
            def errorCollector = new JSErrorCollector(firefoxWebdriver)
        when:
            errorCollector.checkForJavascriptErrors()
        then:
            notThrown(AssertionError)
    }
}
