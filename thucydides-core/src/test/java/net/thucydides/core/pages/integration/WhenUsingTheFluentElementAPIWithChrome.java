package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheFluentElementAPIWithChrome extends AbstractWhenUsingTheFluentElementAPI {


    @BeforeClass
    public static void initDriver() {
        driver = new WebDriverFacade(ChromeDriver.class, new WebDriverFactory());
        page = new StaticSitePage(driver, 1);
        page.setWaitForTimeout(2000);
        page.open();
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
    }

    @Override
    @Ignore
    @Test
    public void should_optionally_type_enter_after_entering_text() {}

    @Test
    public void should_optionally_type_enter_after_entering_text_which_will_submit_the_form() {
        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndEnter("joe");

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));
    }
}
