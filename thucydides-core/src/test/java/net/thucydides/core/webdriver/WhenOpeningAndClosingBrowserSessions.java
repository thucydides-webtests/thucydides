package net.thucydides.core.webdriver;


import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
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

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    @Mock
    WebdriverInstanceFactory webdriverInstanceFactory;

    @Mock
    FirefoxDriver firefoxDriver;

    @Mock
    ChromeDriver chromeDriver;

    @Mock
    InternetExplorerDriver ieDriver;

    WebdriverManager webdriverManager;

    WebDriverFactory factory;

    TransparentWebDriverFacade webDriver;

    
    private void initWendriverManager() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        when(webdriverInstanceFactory.newInstanceOf(FirefoxDriver.class)).thenReturn(firefoxDriver);
        when(webdriverInstanceFactory.newInstanceOf(ChromeDriver.class)).thenReturn(chromeDriver);
        when(webdriverInstanceFactory.newInstanceOf(InternetExplorerDriver.class)).thenReturn(ieDriver);
        when(webdriverInstanceFactory.newInstanceOf(eq(FirefoxDriver.class), any(FirefoxProfile.class))).thenReturn(firefoxDriver);

        MockEnvironmentVariables environmentVariables = new MockEnvironmentVariables();
        factory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables);

        webdriverManager = new WebdriverManager(factory);
    }


    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        initWendriverManager();
        
        webDriver = new TransparentWebDriverFacade((WebDriverFacade)webdriverManager.getWebdriver());
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
