package net.thucydides.core.pages

import net.thucydides.core.annotations.DefaultUrl
import net.thucydides.core.annotations.NamedUrl
import net.thucydides.core.annotations.NamedUrls
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.SystemPropertiesConfiguration
import org.openqa.selenium.WebDriver
import spock.lang.Specification
import spock.lang.Unroll

class WhenSwitchingPages extends Specification {

    static class UnannotatedPageObject {}

    static class PageObjectA extends PageObject {

        PageObjectA(WebDriver driver) {
            super(driver)
        }

        PageObjectB switchToPageB() {
            switchToPage(PageObjectB.class)
        }
    }

    static class PageObjectB extends PageObject {

        PageObjectB(WebDriver driver) {
            super(driver)
        }
    }

    def driver = Mock(WebDriver)

    def "should be able to switch between pages from withing a page"() {
        given:
            Pages pageFactory = new Pages(driver);
            def pageA = pageFactory[PageObjectA]
        when:
            def pageB = pageA.switchToPageB()
        then:
            pageB instanceof PageObjectB
    }
}