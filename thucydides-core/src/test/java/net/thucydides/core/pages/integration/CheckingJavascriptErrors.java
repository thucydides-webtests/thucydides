package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.firefox.JSErrorCollector;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

public class CheckingJavascriptErrors extends FluentElementAPITestsBaseClass {

    @Test(expected = AssertionError.class)
    public void should_report_javascript_errors_if_present() {

        WebDriverFactory webDriverFactory = new WebDriverFactory();
        WebDriver driver = webDriverFactory.newInstanceOf(SupportedWebDriver.FIREFOX);
        StaticSitePage page = new StaticSitePage(driver, 1);

        page.open();
        page.triggerBrokenJavascript();
        new JSErrorCollector(page.getDriver()).checkForJavascriptErrors();
    }

}
