package net.thucydides.core.pages;

import static org.mockito.Mockito.verify;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

public class WhenKeepingTrackOfVisitedPages {

    @Mock
    WebDriver driver;
    
    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void the_pages_object_should_have_a_default_starting_point_url() {

        final String baseUrl = "http://www.google.com";
        final Pages pages = new Pages(driver);
        
        PageConfiguration.getCurrentConfiguration().setDefaultBaseUrl("http://www.google.com");
        pages.start();
        
        verify(driver).get(baseUrl);    
    }
    
    @Test
    public void the_default_starting_point_url_can_be_overriden_by_a_system_property() {

        final String defaultBaseUrl = "http://www.google.com";
        final String systemDefinedBaseUrl = "http://www.google.com.au";
        final Pages pages = new Pages(driver);
        
        PageConfiguration.getCurrentConfiguration().setDefaultBaseUrl(defaultBaseUrl);
        System.setProperty("webdriver.base.url", systemDefinedBaseUrl);
        
        pages.start();
        
        verify(driver).get(systemDefinedBaseUrl);    
    }
}
