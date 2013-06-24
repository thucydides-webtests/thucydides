package net.thucydides.core.annotations.findby

import net.thucydides.core.annotations.DefaultUrl
import net.thucydides.core.pages.PageObject
import net.thucydides.core.pages.WebElementFacade
import net.thucydides.core.webdriver.DefaultPageObjectInitialiser
import net.thucydides.core.webdriver.WebDriverFacade
import net.thucydides.core.webdriver.WebDriverFactory
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import spock.lang.Shared
import spock.lang.Specification

class WhenUsingSmartFindByOnPageObjects extends Specification {
		
	@DefaultUrl("classpath:static-site/index.html")
	class StaticSitePageWithFindBy extends PageObject {
	
			@FindBy(jquery = "[name='firstname']")
			public WebElementFacade firstName;
	
			@FindBy(jquery = "[name='hiddenfield']")
			public WebElementFacade hiddenField;
			
			@FindBy(jquery = ".doesNotExist")
			public WebElementFacade nonExistantField;
			
			StaticSitePageWithFindBy(WebDriver driver){
				super(driver);
			}
	}

	@Shared
	def driver =  new WebDriverFacade(FirefoxDriver, new WebDriverFactory());

	@Shared
	StaticSitePageWithFindBy page =  new StaticSitePageWithFindBy(driver);

	def setupSpec() {
		new DefaultPageObjectInitialiser(driver, 1000).apply(page);
		page.open()
		page.waitFor(2).seconds()
	}
	
	def "should be able to find an element using jquery immediately"(){

		when: "page is opened"	

		then: "we should find the element immediately"
			page.firstName.isCurrentlyVisible()
	}
	
	def "the response should be immediate when element is not visible using jquery"(){

		when: "page is opened"

		then: "we should know that the element is not visible immediately"
			!page.hiddenField.isCurrentlyVisible()
	}
	
	def "the response should be immediate when element does not exist using jquery"(){

		when: "page is opened"

		then: "we should know that the element is not visible immediately"
			!page.nonExistantField.isCurrentlyVisible()
	}

	def cleanupSpec() {
		if (driver) {
			driver.close()
		}
	}

}
