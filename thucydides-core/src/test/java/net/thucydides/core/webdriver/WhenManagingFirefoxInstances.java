package net.thucydides.core.webdriver;

import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.ThucydidesWebdriverManager;
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
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenManagingFirefoxInstances {
    @Mock
    WebdriverInstanceFactory webdriverInstanceFactory;

    @Mock
    FirefoxDriver firefoxDriver;

    @Mock
    ChromeDriver chromeDriver;

    @Mock
    InternetExplorerDriver ieDriver;

    @Mock
    HtmlUnitDriver htmlUnitDriver;

    @Mock
    FirefoxProfile firefoxProfile;

    @Mock
    RemoteWebDriver remoteWebDriver;

    MockEnvironmentVariables environmentVariables;

    WebdriverManager webdriverManager;

    WebDriverFactory factory;

    Configuration configuration;

    private void initWebdriverManager() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        when(webdriverInstanceFactory.newInstanceOf(FirefoxDriver.class)).thenReturn(firefoxDriver);
        when(webdriverInstanceFactory.newInstanceOf(ChromeDriver.class)).thenReturn(chromeDriver);
        when(webdriverInstanceFactory.newInstanceOf(HtmlUnitDriver.class)).thenReturn(htmlUnitDriver);
        when(webdriverInstanceFactory.newInstanceOf(InternetExplorerDriver.class)).thenReturn(ieDriver);
        when(webdriverInstanceFactory.newInstanceOf(eq(ChromeDriver.class), any(ChromeOptions.class))).thenReturn(chromeDriver);
        when(webdriverInstanceFactory.newInstanceOf(eq(FirefoxDriver.class), any(FirefoxProfile.class))).thenReturn(firefoxDriver);
        when(webdriverInstanceFactory.newInstanceOf(eq(RemoteWebDriver.class))).thenReturn(remoteWebDriver);

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
        initWebdriverManager();
        StepEventBus.getEventBus().clearStepFailures();
    }

    @After
    public void closeDriver() {
        webdriverManager.closeAllDrivers();
    }


    @Test
    public void a_firefox_instance_will_not_assume_untrusted_certificates_if_requested() throws Exception {

        environmentVariables.setProperty("webdriver.driver", "firefox");
        environmentVariables.setProperty(SystemPropertiesConfiguration.ASSUME_UNTRUSTED_CERTIFICATE_ISSUER, "false");

        WebdriverManager webdriverManager = new ThucydidesWebdriverManager(factory, configuration);

        WebDriver driver = webdriverManager.getWebdriver();
        driver.get(staticSiteUrl());

        verify(firefoxProfile).setAssumeUntrustedCertificateIssuer(false);
    }

    @Test
    public void a_firefox_instance_will_assume_untrusted_certificates_by_default() throws Exception {

        environmentVariables.setProperty("webdriver.driver", "firefox");

        WebdriverManager webdriverManager = new ThucydidesWebdriverManager(factory, configuration);

        WebDriver driver = webdriverManager.getWebdriver();
        driver.get(staticSiteUrl());

        verify(firefoxProfile, never()).setAssumeUntrustedCertificateIssuer(false);
    }


    @Test
    public void a_firefox_webdriver_instance_is_created_by_default_if_no_webdriver_system_property_is_set() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();

        driver.get(staticSiteUrl());

        verify(webdriverInstanceFactory).newInstanceOf(eq(FirefoxDriver.class), any(FirefoxProfile.class));
    }


    @Test
    public void a_new_chrome_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_chrome() throws Exception {

        environmentVariables.setProperty("webdriver.driver", "chrome");

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();
        driver.get(staticSiteUrl());

        assertThat(driver.proxiedWebDriver, instanceOf(ChromeDriver.class));

    }

    @Test
    public void a_new_ie_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_iexplorer() throws Exception {

        environmentVariables.setProperty("webdriver.driver", "iexplorer");

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();
        driver.get(staticSiteUrl());

        assertThat(driver.proxiedWebDriver, instanceOf(InternetExplorerDriver.class));

    }

    @Test
    public void a_new_firefox_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_firefox() {

        environmentVariables.setProperty("webdriver.driver","firefox");

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();
        driver.get(staticSiteUrl());
        assertThat(driver.proxiedWebDriver, instanceOf(FirefoxDriver.class));
    }



    private String staticSiteUrl() {
        return "file://" + Thread.currentThread().getContextClassLoader().getResource("static-site/index.html").toString();
    }

}
