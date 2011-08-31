package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheFluentElementAPIWithFirefox extends AbstractWhenUsingTheFluentElementAPI {

    @BeforeClass
    public static void initDriver() {
        driver = new WebDriverFacade(FirefoxDriver.class, new WebDriverFactory());
        page = new StaticSitePage(driver, 1);
        page.setWaitForTimeout(100);
        page.open();
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
    }
    @Ignore("WebDriver doesn't like tabs at the moment")
    @Test
    public void should_optionally_type_tab_after_entering_text() {
        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndTab("joe");

        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }
}
