package net.thucydides.core.webdriver.integration;

import net.thucydides.core.csv.Person;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.FirefoxFactory;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class WhenManagingWebdriverInstances {

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    private WebDriverFactory factory;
    
    @Mock
    private WebDriver webDriver;

    @Mock
    private WebDriver newWebDriver;

    @Mock
    private FirefoxFactory mockFirefoxFactory;

    private Configuration config;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        config = new Configuration();
        factory = new WebDriverFactory();
    }
    
    @Test
    public void a_new_firefox_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_firefox() {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "firefox");
        WebdriverManager webdriverManager = new WebdriverManager(factory);

        Class driverClass = webdriverManager.getWebDriverClass();
        assertThat(driverClass.getName(), is(FirefoxDriver.class.getName()));
    }
    
    @Test
    public void a_firefox_instance_will_use_a_profile_compatible_with_untrusted_certificates_if_requested() {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "firefox");
        System.setProperty(Configuration.UNTRUSTED_CERTIFICATES, "true");

        WebDriverFactory testableFactory = new WebDriverFactory() {
            protected FirefoxFactory getFirefoxFactory() {
                return mockFirefoxFactory;
            }
        };

        testableFactory.newInstanceOf(SupportedWebDriver.FIREFOX);;

        verify(mockFirefoxFactory).newUntrustedCertificateCompatibleDriver();
    }

    @Test
    public void a_firefox_instance_will_use_a_normal_profile_if_requested() {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "firefox");
        System.setProperty(Configuration.UNTRUSTED_CERTIFICATES, "false");

        WebDriverFactory testableFactory = new WebDriverFactory() {
            protected FirefoxFactory getFirefoxFactory() {
                return mockFirefoxFactory;
            }
        };

        WebDriver driver = testableFactory.newInstanceOf(SupportedWebDriver.FIREFOX);;
        driver.quit();

        verify(mockFirefoxFactory, never()).newUntrustedCertificateCompatibleDriver();
    }

    @Test
    public void a_firefox_instance_will_use_a_normal_profile_by_default() {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "firefox");

        WebDriverFactory testableFactory = new WebDriverFactory() {
            protected FirefoxFactory getFirefoxFactory() {
                return mockFirefoxFactory;
            }
        };

        WebDriver driver = testableFactory.newInstanceOf(SupportedWebDriver.FIREFOX);;
        driver.quit();

        verify(mockFirefoxFactory, never()).newUntrustedCertificateCompatibleDriver();
    }

    @Test
    public void a_new_chrome_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_chrome() {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "chrome");
        WebdriverManager webdriverManager = new WebdriverManager(factory);

        Class driverClass = webdriverManager.getWebDriverClass();
        assertThat(driverClass.getName(), is(ChromeDriver.class.getName()));
    }



    @Test
    public void the_default_output_directory_can_be_overrided_via_a_system_property() {
        System.setProperty(Configuration.OUTPUT_DIRECTORY_PROPERTY, "out");
        
        assertThat(config.getOutputDirectory().getName(), is("out"));
    }

}
