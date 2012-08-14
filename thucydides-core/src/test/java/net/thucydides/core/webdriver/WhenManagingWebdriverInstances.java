package net.thucydides.core.webdriver;

import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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

public class WhenManagingWebdriverInstances {

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
    }

    @After
    public void closeDriver() {
        webdriverManager.closeAllDrivers();
    }

    @Test
    public void a_firefox_webdriver_instance_is_created_by_default_if_no_webdriver_system_property_is_set() {

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();

        driver.get(staticSiteUrl());

        assertThat(driver.proxiedWebDriver, instanceOf(FirefoxDriver.class));
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

        WebDriverFacade uppercaseFirefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("HtmlUnit");
        WebDriverFacade firefoxDriver = (WebDriverFacade) webdriverManager.getWebdriver("htmlunit");

        assertThat(firefoxDriver, is(uppercaseFirefoxDriver));
    }

    @Test
    public void a_new_firefox_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_firefox() {

        environmentVariables.setProperty("webdriver.driver","firefox");

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();
        driver.get(staticSiteUrl());
        assertThat(driver.proxiedWebDriver, instanceOf(FirefoxDriver.class));
    }
    
    @Test
    public void a_new_htmlunit_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_htmlunit() {

        environmentVariables.setProperty("webdriver.driver","htmlunit");

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();
        driver.get(staticSiteUrl());
        assertThat(driver.proxiedWebDriver, instanceOf(HtmlUnitDriver.class));
    }
    @Test
    public void the_configured_driver_type_can_be_overriden_for_a_particular_test() {

        environmentVariables.setProperty("webdriver.driver","firefox");

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver("chrome");
        driver.get(staticSiteUrl());
        assertThat(driver.proxiedWebDriver, instanceOf(ChromeDriver.class));
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
    public void a_firefox_instance_will_accept_untrusted_certificates_by_default() throws Exception {

        environmentVariables.setProperty("webdriver.driver", "firefox");

        WebdriverManager webdriverManager = new ThucydidesWebdriverManager(factory, configuration);

        WebDriver driver = webdriverManager.getWebdriver();
        driver.get(staticSiteUrl());

        verify(firefoxProfile,never()).setAcceptUntrustedCertificates(false);
    }


    @Test
    public void a_new_chrome_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_chrome() throws Exception {

        environmentVariables.setProperty("webdriver.driver", "chrome");
        initWebdriverManager();

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();
        driver.get(staticSiteUrl());

        assertThat(driver.proxiedWebDriver, instanceOf(ChromeDriver.class));

    }

    @Test
    public void a_new_ie_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_iexplorer() throws Exception {

        environmentVariables.setProperty("webdriver.driver", "iexplorer");
        initWebdriverManager();

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();
        driver.get(staticSiteUrl());

        assertThat(driver.proxiedWebDriver, instanceOf(InternetExplorerDriver.class));

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
