package net.thucydides.core.webdriver
import sample.page.NotImplementedInterfacePageimport sample.page.WithBadImplementerPageimport sample.page.WithFindByOnClassPageimport spock.lang.Specificationimport net.thucydides.core.annotations.NotImplementedExceptionimport org.openqa.selenium.WebDriver

class WhenExtendingWebElementFacade extends Specification {		def driver = Mock(WebDriver)		def "should fail gracefully when interface is not implemented"(){		when:			def page = new NotImplementedInterfacePage(driver)		then:			thrown(NotImplementedException)	}		def "should fail gracefully when FindBy annotation is on Class"(){		when:			def page = new WithFindByOnClassPage(driver)		then:			thrown(NotImplementedException)	}		def "should fail gracefully when inteface ImplementedBy Class is not implemented"(){		when:			def page = new WithBadImplementerPage(driver)		then:			true			NotImplementedException exptn = thrown()			exptn.message.contains("does not implement the interface")	}

}
