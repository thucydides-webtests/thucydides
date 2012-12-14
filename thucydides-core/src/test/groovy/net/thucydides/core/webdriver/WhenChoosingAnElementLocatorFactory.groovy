package net.thucydides.core.webdriver

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory
import spock.lang.Specification

class WhenChoosingAnElementLocatorFactory extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
    def driver = Mock(WebDriver)

    def "should choose the DisplayedElementLocatorFactory factory by default"() {
        given:
            def configuration = new SystemPropertiesConfiguration(environmentVariables);
            def selectorFactory = new ElementLocatorFactorySelector(configuration);
        when:
            def locator = selectorFactory.getLocatorFor(driver)
        then:
            locator.class == DisplayedElementLocatorFactory
    }

    def "should choose the AjaxElementLocatorFactory factory if requested"() {
        given:
            environmentVariables.setProperty("thucydides.locator.factory","AjaxElementLocatorFactory")
            def configuration = new SystemPropertiesConfiguration(environmentVariables);
            def selectorFactory = new ElementLocatorFactorySelector(configuration);
        when:
            def locator = selectorFactory.getLocatorFor(driver)
        then:
            locator.class == AjaxElementLocatorFactory
    }

    def "should choose the DefaultElementLocatorFactory factory if requested"() {
        given:
            environmentVariables.setProperty("thucydides.locator.factory","DefaultElementLocatorFactory")
            def configuration = new SystemPropertiesConfiguration(environmentVariables);
            def selectorFactory = new ElementLocatorFactorySelector(configuration);
        when:
            def locator = selectorFactory.getLocatorFor(driver)
        then:
            locator.class == DefaultElementLocatorFactory
    }

    def "should throw exception with meaningful error if an invalid factory class is specified"() {
        given:
            environmentVariables.setProperty("thucydides.locator.factory","UnknownFactory")
            def configuration = new SystemPropertiesConfiguration(environmentVariables);
            def selectorFactory = new ElementLocatorFactorySelector(configuration);
        when:
            def locator = selectorFactory.getLocatorFor(driver)
        then:
            IllegalArgumentException e = thrown()
        and:
            e.message.contains("UnknownFactory")
    }
}
