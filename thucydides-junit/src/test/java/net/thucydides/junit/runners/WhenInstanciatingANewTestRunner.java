package net.thucydides.junit.runners;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.UnsupportedDriverException;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.integration.samples.ManagedWebDriverSample;
import net.thucydides.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.junit.runners.mocks.MockThucydidesRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.rules.ExpectedException;
import org.junit.rules.MethodRule;
import org.junit.runners.model.InitializationError;

/**
 * Instanciating new Webdriver instances. When using the Thucydides test runner,
 * new WebDriver driver instances are instanciated based on system properties.
 * 
 * @author johnsmart
 * 
 */
public class WhenInstanciatingANewTestRunner {

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    @Test
    public void the_default_driver_should_be_firefox() throws InitializationError {

        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);

        runner.newDriver();

        verify(mockBrowserFactory, times(1)).newInstanceOf(SupportedWebDriver.FIREFOX);
    }

    @Test
    public void we_can_override_the_default_driver_to_use_chrome()
            throws InitializationError {
        
        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);

        System.setProperty("webdriver.driver", "chrome");
        runner.newDriver();
        
        verify(mockBrowserFactory, times(1)).newInstanceOf(SupportedWebDriver.CHROME);
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void system_should_complain_if_we_use_an_unsupported_driver()
            throws InitializationError {
        
        thrown.expect(UnsupportedDriverException.class);
        thrown.expectMessage(JUnitMatchers.containsString("htmlunit is not a supported browser. Supported driver values are:"));
        
        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);

        System.setProperty("webdriver.driver", "htmlunit");
        runner.newDriver();
    }
    
    @Test
    public void iexplored_is_not_currently_a_supported_driver()
            throws InitializationError {
        
        thrown.expect(UnsupportedDriverException.class);
        thrown.expectMessage(JUnitMatchers.containsString("iexplorer is not a supported browser. Supported driver values are:"));
        
        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);

        System.setProperty("webdriver.driver", "iexplorer");
        runner.newDriver();
    }    

    private ThucydidesRunner getTestRunnerUsing(WebDriverFactory browserFactory) throws InitializationError {
        return new MockThucydidesRunner(ManagedWebDriverSample.class, browserFactory);   
    }
    
}
