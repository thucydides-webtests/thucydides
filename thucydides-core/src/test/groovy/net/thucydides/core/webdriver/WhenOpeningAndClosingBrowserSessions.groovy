package net.thucydides.core.webdriver

import com.opera.core.systems.OperaDriver
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.Capabilities
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import spock.lang.Specification
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.nullValue
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.notNullValue
import net.thucydides.core.steps.StepEventBus

class WhenOpeningAndClosingBrowserSessions extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def firefox = Mock(FirefoxDriver)
    def chrome = Mock(ChromeDriver)
    def htmlunit = Mock(HtmlUnitDriver)
    def iexplore = Mock(InternetExplorerDriver)
    def opera = Mock(OperaDriver)
    def remote = Mock(RemoteWebDriver)

    def webdriverInstanceFactory = new WebdriverInstanceFactory() {
        @Override
        WebDriver newFirefoxDriver(FirefoxProfile profile) { return firefox }

        @Override
        WebDriver newChromeDriver(ChromeOptions options) { return chrome }

        @Override
        WebDriver newHtmlUnitDriver(DesiredCapabilities caps) { return htmlunit }

        @Override
        WebDriver newRemoteDriver(URL remoteUrl, DesiredCapabilities capabilities) { return remote }

        @Override
        WebDriver newInstanceOf(Class<? extends WebDriver> webdriverClass) {
            switch (webdriverClass) {
                case InternetExplorerDriver : return iexplore
                case OperaDriver : return opera
            }
        }
    }

    WebDriverFactory webDriverFactory
    WebdriverManager webdriverManager;

    Capabilities capabilities = Mock(Capabilities)

    def setup() {
        capabilities.asMap() >> [:]
        remote.getCapabilities() >> capabilities
        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables)
        webdriverManager = new ThucydidesWebdriverManager(webDriverFactory, new SystemPropertiesConfiguration(environmentVariables));
        StepEventBus.eventBus.clear()
    }

    def "should open a new browser when a page is opened"() {
        given:
            def webdriver = webdriverManager.getWebdriver()
        when:
            webdriver.get("about:blank")
        then:
            webdriver.proxiedWebDriver != null
    }

    def "should shutdown browser when requested"() {
        given:
            def webdriver = webdriverManager.getWebdriver()
        when:
            webdriver.get("about:blank")
            webdriver.quit()
        then:
            webdriver.proxiedWebDriver == null
    }

    def "should open a new browser after shutdown browser when requested"() {
        given:
            def webdriver = webdriverManager.getWebdriver()
        when:
            webdriver.get("about:blank")
            webdriver.quit()
        webdriver.get("about:blank")
        then:
            webdriver.proxiedWebDriver != null
    }

    def "quitting a shut browser should have no effect"() {
        given:
            def webdriver = webdriverManager.getWebdriver()
            webdriver.get("about:blank")
            webdriver.quit()
        when:
            webdriver.quit()
        then:
            webdriver.proxiedWebDriver == null
    }

    def "resetting the proxy should close the current browser"() {
        given:
            def webdriver = webdriverManager.getWebdriver()
            webdriver.get("about:blank")
        when:
            webdriver.reset()
        then:
            webdriver.proxiedWebDriver == null
    }

}
