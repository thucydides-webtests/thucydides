package net.thucydides.core.webdriver

import com.opera.core.systems.OperaDriver
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.Capabilities
import org.openqa.selenium.WebDriver
import org.openqa.selenium.android.library.DriverProvider
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.server.DriverFactory
import spock.lang.Ignore
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
