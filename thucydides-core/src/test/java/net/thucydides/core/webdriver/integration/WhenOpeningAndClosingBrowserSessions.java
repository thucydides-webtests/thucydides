package net.thucydides.core.webdriver.integration;


import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.ThucydidesWebdriverManager;
import net.thucydides.core.webdriver.TransparentWebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverInstanceFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class WhenOpeningAndClosingBrowserSessions {

    WebdriverManager webdriverManager;

    WebDriverFactory factory;

    TransparentWebDriverFacade webDriver;

    WebdriverInstanceFactory webdriverInstanceFactory;

    private void initWendriverManager() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        webdriverInstanceFactory = new WebdriverInstanceFactory();
        factory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables);
        webdriverManager = new ThucydidesWebdriverManager(factory, new SystemPropertiesConfiguration(environmentVariables));
    }


    @Before
    public void createATestableDriverFactory() throws Exception {
        initWendriverManager();
        StepEventBus.getEventBus().clearStepFailures();

        webDriver = new TransparentWebDriverFacade((WebDriverFacade)webdriverManager.getWebdriver("htmlunit"));
    }

    @After
    public void closeDriver() {
        webdriverManager.closeDriver();
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

}
