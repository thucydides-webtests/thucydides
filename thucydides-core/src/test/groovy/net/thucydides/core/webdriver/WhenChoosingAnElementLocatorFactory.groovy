package net.thucydides.core.webdriver

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory
import net.thucydides.core.webdriver.smart.SmartElementLocatorFactory
import spock.lang.Specification
import spock.lang.Unroll;

class WhenChoosingAnElementLocatorFactory extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
    def driver = Mock(WebDriver)
	
	def "should choose the SmartElementLocatorFactory factory by default"() {
        given:
            def configuration = new SystemPropertiesConfiguration(environmentVariables);
            def selectorFactory = new ElementLocatorFactorySelector(configuration);
        when:
            def locator = selectorFactory.getLocatorFor(driver)
        then:
            locator.class == SmartElementLocatorFactory
    }
    
	@Unroll
    def "should choose the #factoryName factory if requested"() {
        given:
        	environmentVariables.setProperty("thucydides.locator.factory", factoryName)
            def configuration = new SystemPropertiesConfiguration(environmentVariables);
            def selectorFactory = new ElementLocatorFactorySelector(configuration);
        when:
            def locator = selectorFactory.getLocatorFor(driver)
        then:
            locator.class == locatorFactoryClass
		where:
			factoryName << ["DisplayedElementLocatorFactory", "AjaxElementLocatorFactory", "DefaultElementLocatorFactory"]
			locatorFactoryClass << [DisplayedElementLocatorFactory, AjaxElementLocatorFactory, DefaultElementLocatorFactory]
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
