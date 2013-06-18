package net.thucydides.core.webdriver

import static org.junit.Assert.*

import spock.lang.Shared
import spock.lang.Specification

import sample.page.TestPage

import org.openqa.selenium.chrome.ChromeDriver

class WhenUsingWebElementFacadeExtender extends Specification {
	
	@Shared
	def driver =  new WebDriverFacade(ChromeDriver, new WebDriverFactory())
	
	@Shared
	def page = new TestPage(driver)
	
	def setupSpec() {
		
		new DefaultPageObjectInitialiser(driver, 1000).apply(page);
		page.open()
		page.waitFor(1).second()
	}
	
	
	def "WebElementFacade methods should be able to be called on Extender"(){
		when: "calling WebElementFacade method"
			
		then: "should be displayed"
			page.elementFirst.isCurrentlyVisible()
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
	
