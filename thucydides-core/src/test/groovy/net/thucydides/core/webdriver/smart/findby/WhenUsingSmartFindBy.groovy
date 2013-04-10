package net.thucydides.core.webdriver.smart.findby

import net.thucydides.core.pages.integration.StaticSitePageWithFacades;
import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.WebDriverFactory

import org.openqa.selenium.NoSuchElementException

import org.openqa.selenium.htmlunit.HtmlUnitDriver

import spock.lang.Specification

class WhenUsingSmartFindBy extends Specification {
	
	def "element can be found by SmartBy with jquery"(){
		
		given: "webdriver navigates to the page with the element"
			def driver = new WebDriverFacade(HtmlUnitDriver.class, new WebDriverFactory());
			def page = new StaticSitePageWithFacades(driver, 1);
			page.setWaitForTimeout(750);
			page.open();
			
		when: "element is tried to be found using jquery selector"
			def element = driver.findElement(SmartBy.jquery("#firstname"))
			
		then: "elemnt is found"
			notThrown(NoSuchElementException)
		
	}

}
