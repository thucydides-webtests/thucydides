package net.thucydides.core.webdriver

import org.openqa.selenium.htmlunit.HtmlUnitDriver
import sample.page.TestPage
import spock.lang.Shared
import spock.lang.Specification

class WhenUsingWebElementFacadeExtender extends Specification {
	
	@Shared
	def driver =  new WebDriverFacade(HtmlUnitDriver, new WebDriverFactory())
	
	@Shared
	def page = new TestPage(driver)
	
	def setupSpec() {
		
		new DefaultPageObjectInitialiser(driver, 1000).apply(page);
		page.open()
	}

    def "WebElementFacade methods can be defined in a page object"(){
        when: "instantiating a page object with WebElementFacade fields"
        then: "the annotated fields should be instantiated"
            page.elementFirst != null

        page.open()
    }

	def "WebElementFacade methods should be able to be called on Extender"(){
		when: "calling WebElementFacade method"

		then: "field should be accessible"
			page.elementFirst.getTagName() == "input"
	}

	def "Extender methods should be able to be called"(){
		when: "calling WebElementFacadeInput method"
			page.elementLast.enterText("text")
		then: "text should be entered"
			page.elementLast.getAttribute("value").equals("text")
	}
	
	def cleanupSpec() {
		if (driver) {
			driver.close()
		}
	}
}
	
