package net.thucydides.core.annotations.findby

import net.thucydides.core.pages.integration.StaticSitePageWithFacades
import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.WebDriverFactory
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.firefox.FirefoxDriver
import spock.lang.Shared
import spock.lang.Specification

class WhenUsingSmartFindBy extends Specification {

    @Shared
    def driver =  new WebDriverFacade(FirefoxDriver, new WebDriverFactory());

    @Shared
    def page = new StaticSitePageWithFacades(driver, 1000)

    def setupSpec() {
        page.open()
        page.waitFor(1).second()
    }

	def "should be able to find an element using jquery"(){

		when: "we try to find an element using a jquery selector"
			def element = driver.findElement(By.jquery("#firstname"))

        then: "we should find the element"
            element.getAttribute("value")  == "<enter first name>"
	}

    def "should be able to find a nested element using jquery"(){

        when: "we try to find an element using a jquery selector"
        def element = driver.findElement(By.jquery("#firstname"))

        then: "we should find the element"
        element.getAttribute("value")  == "<enter first name>"
    }

    def "should be able to find multiple elements using jquery"(){

        when: "we try to find several elements using a jquery selector"
            def optionList = driver.findElements(By.jquery("#multiselect option"))

        then: "we should find a list of elements"
            optionList.size == 5
    }


    def "an element should fail gracefully if the jquery search fails"(){

        when: "we try to find an element using a jquery selector"
            driver.findElement(By.jquery("#does_not_exist"))

        then: "element should be found"
            thrown(NoSuchElementException)
    }

    def "an element should fail gracefully if the jquery search for multiple elements fails"(){

        when: "we try to find an element using a jquery selector"
            driver.findElements(By.jquery("#does_not_exist"))

        then: "element should be found"
            thrown(NoSuchElementException)
    }

    def cleanupSpec() {
        if (driver) {
            driver.close()
        }
    }



}
