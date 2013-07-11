package net.thucydides.core.annotations.findby

import spock.lang.Shared
import spock.lang.Specification

import sample.page.TestPage

import net.thucydides.core.webdriver.DefaultPageObjectInitialiser
import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.WebDriverFactory
import org.openqa.selenium.chrome.ChromeDriver

class WhenFindingExtendedWebElementFacade extends Specification {
	
	@Shared
	def driver =  new WebDriverFacade(ChromeDriver, new WebDriverFactory())
		
	def "WebElementFacade Extender should be found with selenium FindBy"(){
		
		when: "page is initialized"
			def page = new TestPage(driver)
			
		then: "WebElementFacade extender with Smart FindBy should be proxied"
			page.elementFirst != null
			
	}
	
	def "WebElementFacade Extender should be found with Smart FindBy"(){
		
		when: "page is initialized"
			def page = new TestPage(driver)
			
		then: "WebElementFacade extender with Smart FindBy should be proxied"
			page.elementLast != null
			
	}


}
