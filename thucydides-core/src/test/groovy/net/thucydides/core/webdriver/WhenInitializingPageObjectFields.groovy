package net.thucydides.core.webdriver

import net.thucydides.core.annotations.findby.FindBy
import net.thucydides.core.annotations.locators.SmartElementLocatorFactory
import net.thucydides.core.pages.PageObject
import net.thucydides.core.pages.WebElementFacade
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory
import spock.lang.Specification

class WhenInitializingPageObjectFields extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();
    def driver = Mock(WebDriver)

    class SamplePageObject extends PageObject {

        WebElement someField;

        WebElementFacade someFieldFacade;

        @FindBy(css="li")
        List<WebElement> someFieldList;

        @FindBy(css="li")
        List<WebElementFacade> someFieldFacadeList;

        SamplePageObject(WebDriver driver) {
            super(driver)
        }
    }

	def "should initialize WebElement page object fields"() {
        given:
            def pageObjectInitialiser = new DefaultPageObjectInitialiser(driver, 1000);
            def page = new SamplePageObject(driver)
        when:
            pageObjectInitialiser.apply(page);
        then:
            page.someField != null
    }

    def "should initialize WebElementFacade page object fields"() {
        given:
            def pageObjectInitialiser = new DefaultPageObjectInitialiser(driver, 1000);
            def page = new SamplePageObject(driver)
        when:
            pageObjectInitialiser.apply(page);
        then:
            page.someFieldFacade != null
    }

    def "should initialize WebElement list fields"() {
        given:
            def pageObjectInitialiser = new DefaultPageObjectInitialiser(driver, 1000);
            def page = new SamplePageObject(driver)
        when:
            pageObjectInitialiser.apply(page);
        then:
            page.someFieldList != null
    }

    def "should initialize WebElementFacade list fields"() {
        given:
            def pageObjectInitialiser = new DefaultPageObjectInitialiser(driver, 1000);
            def page = new SamplePageObject(driver)
        when:
            pageObjectInitialiser.apply(page);
        then:
            page.someFieldFacadeList != null
    }

    def "should default to at least 1 second AJAX timeout"() {
        when:
            def pageObjectInitialiser = new DefaultPageObjectInitialiser(driver, 5);
        then:
            pageObjectInitialiser.ajaxTimeoutInSecondsWithAtLeast1Second() == 1
    }

    def "should convert timeout to seconds"() {
        when:
           def pageObjectInitialiser = new DefaultPageObjectInitialiser(driver, 5000);
        then:
            pageObjectInitialiser.ajaxTimeoutInSecondsWithAtLeast1Second() == 5
    }

}
