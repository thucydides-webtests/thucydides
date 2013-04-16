package net.thucydides.core.webdriver.smart.findby

import net.thucydides.core.pages.integration.StaticSitePageWithFacades
import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.WebDriverFactory
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Shared
import spock.lang.Specification

class WhenUsingSmartFindBy extends Specification {

    @Shared
    def driver =  new WebDriverFacade(HtmlUnitDriver, new WebDriverFactory());

    @Shared
    def page = new StaticSitePageWithFacades(driver, 1)

    def setupSpec() {
        page.open()
    }

	def "should be able to find an element using jquery"(){

		when: "we try to find an element using a jquery selector"
			def element = driver.findElement(SmartBy.jquery("#firstname"))

        then: "we should find the element"
            element.getAttribute("value")  == "<enter first name>"
	}

    def "should be able to find a nested element using jquery"(){

        when: "we try to find an element using a jquery selector"
        def element = driver.findElement(SmartBy.jquery("#firstname"))

        then: "we should find the element"
        element.getAttribute("value")  == "<enter first name>"
    }

    def "should be able to find multiple elements using jquery"(){

        when: "we try to find several elements using a jquery selector"
            def optionList = driver.findElements(SmartBy.jquery("#multiselect option"))

        then: "we should find a list of elements"
            optionList.size == 5
    }


    def "an element should fail gracefully if the jquery search fails"(){

        when: "we try to find an element using a jquery selector"
            driver.findElement(SmartBy.jquery("#does_not_exist"))

        then: "element should be found"
            thrown(NoSuchElementException)
    }

    def "an element should fail gracefully if the jquery search for multiple elements fails"(){

        when: "we try to find an element using a jquery selector"
            driver.findElements(SmartBy.jquery("#does_not_exist"))

        then: "element should be found"
            thrown(NoSuchElementException)
    }

    def cleanupSpec() {
        if (driver) {
            driver.close()
        }
    }



}
