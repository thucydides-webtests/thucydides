package net.thucydides.core.webdriver;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
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
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WhenManagingWebdriverInstances {

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

    @Mock
    FirefoxProfile firefoxProfile;

    WebdriverManager webdriverManager;

    WebDriverFactory factory;

    private void initWendriverManager() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        when(webdriverInstanceFactory.newInstanceOf(FirefoxDriver.class)).thenReturn(firefoxDriver);
        when(webdriverInstanceFactory.newInstanceOf(ChromeDriver.class)).thenReturn(chromeDriver);
        when(webdriverInstanceFactory.newInstanceOf(InternetExplorerDriver.class)).thenReturn(ieDriver);
        when(webdriverInstanceFactory.newInstanceOf(eq(FirefoxDriver.class), any(FirefoxProfile.class))).thenReturn(firefoxDriver);

        factory = new WebDriverFactory(webdriverInstanceFactory) {
            @Override
            protected FirefoxProfile createNewFirefoxProfile() {
                return firefoxProfile;
            }
        };

        webdriverManager = new WebdriverManager(factory);
    }


    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);
        initWendriverManager();
    }

    @Test
    public void a_firefox_webdriver_instance_is_created_by_default_if_no_webdriver_system_property_is_set() {

        WebdriverManager webdriverManager = new WebdriverManager(factory);
        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();

        driver.get("http://www.google.com");

        assertThat(driver.proxiedWebDriver, instanceOf(FirefoxDriver.class));
    }
    @Test
    public void a_new_firefox_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_firefox() {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "firefox");
        WebdriverManager webdriverManager = new WebdriverManager(factory);

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();
        driver.get("http://www.google.com");
        assertThat(driver.proxiedWebDriver, instanceOf(FirefoxDriver.class));
    }
    
    @Test
    public void a_firefox_instance_will_not_assume_untrusted_certificates_if_requested() throws Exception {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "firefox");
        System.setProperty(Configuration.ASSUME_UNTRUSTED_CERTIFICATE_ISSUER, "false");

        WebdriverManager webdriverManager = new WebdriverManager(factory);

        WebDriver driver = webdriverManager.getWebdriver();
        driver.get("http://www.google.com");

        verify(firefoxProfile).setAssumeUntrustedCertificateIssuer(false);
    }

    @Test
    public void a_firefox_instance_will_assume_untrusted_certificates_by_default() throws Exception {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "firefox");

        WebdriverManager webdriverManager = new WebdriverManager(factory);

        WebDriver driver = webdriverManager.getWebdriver();
        driver.get("http://www.google.com");

        verify(firefoxProfile, never()).setAssumeUntrustedCertificateIssuer(false);
    }

    @Test
    public void a_firefox_instance_will_accept_untrusted_certificates_by_default() throws Exception {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "firefox");

        WebdriverManager webdriverManager = new WebdriverManager(factory);

        WebDriver driver = webdriverManager.getWebdriver();
        driver.get("http://www.google.com");

        verify(firefoxProfile,never()).setAcceptUntrustedCertificates(false);
    }

    @Test
    public void a_new_chrome_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_chrome() throws Exception {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "chrome");
        initWendriverManager();

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();
        driver.get("http://www.google.com");

        System.out.println(System.getProperty("webdriver.driver"));
        assertThat(driver.proxiedWebDriver, instanceOf(ChromeDriver.class));

    }

    @Test
    public void a_new_ie_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_iexplorer() throws Exception {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "iexplorer");
        initWendriverManager();

        WebDriverFacade driver = (WebDriverFacade) webdriverManager.getWebdriver();
        driver.get("http://www.google.com");

        assertThat(driver.proxiedWebDriver, instanceOf(InternetExplorerDriver.class));

    }


    @Test
    public void the_default_output_directory_can_be_overrided_via_a_system_property() {
        System.setProperty(Configuration.OUTPUT_DIRECTORY_PROPERTY, "out");

        Configuration config = new Configuration();

        assertThat(config.getOutputDirectory().getName(), is("out"));
    }

}
