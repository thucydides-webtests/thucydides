package net.thucydides.core.webdriver;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenManagingWebdriverInstances {

    WebdriverInstanceFactory webdriverInstanceFactory;

    MockEnvironmentVariables environmentVariables;
    
    WebdriverManager webdriverManager;

    WebDriverFactory factory;

    Configuration configuration; 

    FirefoxProfile firefoxProfile;

    private void initWebdriverManagers() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        firefoxProfile = mock(FirefoxProfile.class);

        webdriverInstanceFactory = new WebdriverInstanceFactory();
        factory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables) {
            @Override
            protected FirefoxProfile createNewFirefoxProfile() {
                return firefoxProfile;
            }
        };


        configuration = new SystemPropertiesConfiguration(environmentVariables);
        
        webdriverManager = new ThucydidesWebdriverManager(factory, configuration);
        webdriverManager.closeAllDrivers();
    }


    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();
        initWebdriverManagers();
        StepEventBus.getEventBus().clearStepFailures();
    }

    @After
    public void closeDriver() {
        webdriverManager.closeAllDrivers();
    }

    @Test
    public void if_an_empty_driver_type_is_specified_the_default_driver_should_be_used() {

        WebDriverFacade defaultDriver = (WebDriverFacade) webdriverManager.getWebdriver();
        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver("");

        assertThat(driver, is(defaultDriver));
    }

    @Test
    public void if_a_null_driver_type_is_specified_the_default_driver_should_be_used() {

        WebDriverFacade defaultDriver = (WebDriverFacade) webdriverManager.getWebdriver();
        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver(null);

        assertThat(driver, is(defaultDriver));
    }

    @Test
    public void the_default_driver_should_be_the_firefox_driver() {

        WebDriverFacade defaultDriver = (WebDriverFacade) webdriverManager.getWebdriver();
        WebDriverFacade firefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("firefox");

        assertThat(firefoxDriver, is(defaultDriver));
    }

    @Test
    public void driver_names_should_be_case_insensitive() {

        WebDriverFacade uppercaseFirefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("Firefox");
        WebDriverFacade firefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("firefox");

        assertThat(firefoxDriver, is(uppercaseFirefoxDriver));
    }

    @Test
    public void driver_names_for_non_default_drivers_should_be_case_insensitive() {

        WebDriverFacade uppercaseFirefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("htmlUnit");
        WebDriverFacade firefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("htmlunit");

        assertThat(firefoxDriver, is(uppercaseFirefoxDriver));
    }


    @Test
    public void the_default_output_directory_can_be_overrided_via_a_system_property() {
        environmentVariables.setProperty("thucydides.outputDirectory","out");

        SystemPropertiesConfiguration config = new SystemPropertiesConfiguration(environmentVariables);

        assertThat(config.getOutputDirectory().getName(), is("out"));
    }

    private String staticSiteUrl() {
        return "file://" + Thread.currentThread().getContextClassLoader().getResource("static-site/index.html").toString();
    }

}
