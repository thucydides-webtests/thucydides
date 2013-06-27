package net.thucydides.core.webdriver

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import sample.page.TestPage
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class WhenUsingWebElementFacadeExtender extends Specification {
	
	@Shared
	def driver =  new WebDriverFacade(FirefoxDriver, new WebDriverFactory())
	
	@Shared
	def page = new TestPage(driver)
	
	def setupSpec() {
		
		new DefaultPageObjectInitialiser(driver, 1000).apply(page);
		page.open()
		page.waitFor(1).second()
	}


    // TODO: review this test
    @Ignore
	def "WebElementFacade methods should be able to be called on Extender"(){
		when: "calling WebElementFacade method"

		then: "should be displayed"
			page.elementFirst.isCurrentlyVisible()
	}

    // TODO: review this test
    @Ignore
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
	
