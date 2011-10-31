package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheFluentElementAPIWithFirefox extends AbstractWhenUsingTheFluentElementAPI {

    @BeforeClass
    public static void initDriver() {
        driver = new WebDriverFacade(FirefoxDriver.class, new WebDriverFactory());
        page = new StaticSitePage(driver, 1);
        page.setWaitForTimeout(2000);
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

    //
    // Note: The is in the Firefox tests as the Chrome driver does not always seem to return meaningful error messages.
    //
    @Test
    public void should_display_meaningful_error_message_if_waiting_for_field_that_does_not_appear() {

        expectedException.expect(ElementNotVisibleException.class);
        expectedException.expectMessage(allOf(containsString("Unable to locate element"),
                                              containsString("fieldDoesNotExist")));

        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.setWaitForTimeout(1000);
        page.open();

        page.element(page.fieldDoesNotExist).waitUntilVisible();
    }

}


