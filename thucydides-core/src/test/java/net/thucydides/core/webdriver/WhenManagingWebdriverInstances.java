package net.thucydides.core.webdriver;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenManagingWebdriverInstances {

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    private WebDriverFactory factory;
    
    @Mock
    private WebDriver webDriver;

    @Mock
    private WebDriver newWebDriver;

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
    public void a_new_chrome_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_chrome() {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "chrome");
        WebdriverManager webdriverManager = new WebdriverManager(factory);

        Class driverClass = webdriverManager.getWebDriverClass();
        assertThat(driverClass.getName(), is(ChromeDriver.class.getName()));
    }

    @Ignore("Adding experminental support for iexplorer")
    @Test(expected=UnsupportedDriverException.class)
    public void iexplorer_is_not_supported() {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "iexplorer");
        new WebdriverManager(factory);        
    }


    @Test
    public void the_default_output_directory_can_be_overrided_via_a_system_property() {
        System.setProperty(Configuration.OUTPUT_DIRECTORY_PROPERTY, "out");
        
        assertThat(config.getOutputDirectory().getName(), is("out"));
    }

}
