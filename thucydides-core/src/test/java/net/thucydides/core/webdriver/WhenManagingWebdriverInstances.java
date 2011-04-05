package net.thucydides.core.webdriver;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

public class WhenManagingWebdriverInstances {

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    @Mock
    private WebDriverFactory factory;
    
    @Mock
    private WebDriver webDriver;

    private Configuration config;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        config = new Configuration();
        
        when(factory.newInstanceOf(SupportedWebDriver.FIREFOX)).thenReturn(webDriver);
    }
    
    @Test
    public void a_new_webdriver_instance_is_created_when_the_webdriver_manager_is_created() {
        new WebdriverManager(factory);        
        
        verify(factory).newInstanceOf(any(SupportedWebDriver.class));
    }
    
    @Test
    public void a_new_firefox_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_firefox() {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "firefox");
        new WebdriverManager(factory);        
        
        verify(factory).newInstanceOf(SupportedWebDriver.FIREFOX);
    }
    
    @Test
    public void a_new_chrome_webdriver_instance_is_created_when_the_webdriver_system_property_is_set_to_chrome() {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "chrome");
        new WebdriverManager(factory);        
        
        verify(factory).newInstanceOf(SupportedWebDriver.CHROME);
    }

    @Test(expected=UnsupportedDriverException.class)
    public void iexplorer_is_not_supported() {

        System.setProperty(Configuration.WEBDRIVER_DRIVER, "iexplorer");
        new WebdriverManager(factory);        
    }

    @Test
    public void we_can_obtain_the_webdriver_from_the_manager() {

        WebdriverManager manager = new WebdriverManager(factory);        
        assertThat(manager.getWebdriver(), is(webDriver));
    }
    
    @Test
    public void when_the_manager_is_closed_the_browser_quits() {

        WebdriverManager manager = new WebdriverManager(factory);        
        manager.closeDriver();
        
        verify(webDriver).quit();
    }
    
    @Test
    public void the_default_output_directory_can_be_overrided_via_a_system_property() {
        System.setProperty(Configuration.OUTPUT_DIRECTORY_PROPERTY, "out");
        
        assertThat(config.getOutputDirectory().getName(), is("out"));
    }
}
