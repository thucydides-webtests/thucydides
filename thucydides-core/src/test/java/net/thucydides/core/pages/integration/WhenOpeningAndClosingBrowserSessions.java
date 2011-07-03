package net.thucydides.core.pages.integration;


import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class WhenOpeningAndClosingBrowserSessions {

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    private static class TestableWebDriverFacade extends WebDriverFacade {

        public TestableWebDriverFacade(final Class<? extends WebDriver> driverClass) {
            super(driverClass);
        }

        public WebDriver getProxied() {
            return getDriverInstance();
        }
    }

    TestableWebDriverFacade webDriver;

    @Before
    public void createProxiedWebdriver() {
        webDriver = new TestableWebDriverFacade(FirefoxDriver.class);
    }

    @After
    public void shutdownOpenBrowsers() {
        webDriver.quit();
    }

    @Test
    public void the_proxy_should_open_a_new_browser_instance_when_a_page_is_opened() {
        assertThat(webDriver.getProxied(), is(nullValue()));

        webDriver.get("about:blank");

        assertThat(webDriver.getProxied(), is(notNullValue()));

    }

    @Test
    public void the_proxy_should_shutdown_the_browser_instance_when_requested() {
        webDriver.get("about:blank");

        assertThat(webDriver.getProxied(), is(notNullValue()));

        webDriver.quit();

        assertThat(webDriver.getProxied(), is(nullValue()));
    }

    @Test
    public void the_proxy_should_open_a_new_browser_after_shutdown_when_requested() {
        webDriver.get("about:blank");
        webDriver.quit();

        assertThat(webDriver.getProxied(), is(nullValue()));

        webDriver.get("about:blank");

        assertThat(webDriver.getProxied(), is(notNullValue()));

    }

    @Test
    public void quitting_a_shutdown_browser_should_have_no_effect() {
        webDriver.get("about:blank");
        webDriver.quit();

        assertThat(webDriver.getProxied(), is(nullValue()));

        webDriver.quit();

        assertThat(webDriver.getProxied(), is(nullValue()));

    }

    @Test
    public void resetting_the_proxy_should_close_the_current_broswer() {
        webDriver.get("about:blank");
        WebDriver driver1 = webDriver.getProxied();

        webDriver.reset();

        assertThat(webDriver.getProxied(), is(nullValue()));

    }

    @Test
     public void resetting_the_proxy_should_open_a_fresh_broswer() {
         webDriver.get("about:blank");
         WebDriver driver1 = webDriver.getProxied();

         webDriver.reset();
         webDriver.get("about:blank");

         assertThat(webDriver.getProxied(), is(not(driver1)));

     }

}
