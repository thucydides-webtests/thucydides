package net.thucydides.junit.runners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;

import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.UnsupportedDriverException;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.junit.integration.samples.ManagedWebDriverSample;
import net.thucydides.junit.runners.mocks.MockThucydidesRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.rules.ExpectedException;
import org.junit.rules.MethodRule;
import org.junit.runner.notification.RunNotifier;
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

        runner.run(new RunNotifier());

        verify(mockBrowserFactory, times(1)).newInstanceOf(SupportedWebDriver.FIREFOX);
    }

    @Test
    public void we_can_override_the_default_driver_to_use_chrome()
            throws InitializationError {
        
        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);

        System.setProperty("webdriver.driver", "chrome");
        runner.run(new RunNotifier());
        
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
        runner.run(new RunNotifier());
    }
    
    @Test
    public void iexplored_is_not_currently_a_supported_driver()
            throws InitializationError {
        
        thrown.expect(UnsupportedDriverException.class);
        thrown.expectMessage(JUnitMatchers.containsString("iexplorer is not a supported browser. Supported driver values are:"));
        
        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);

        System.setProperty("webdriver.driver", "iexplorer");
        runner.run(new RunNotifier());
    }    

    @Test
    public void the_default_output_directory_should_follow_the_maven_convention() throws InitializationError {

        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);

        File outputDirectory = runner.getOutputDirectory();
        
        assertThat(outputDirectory.getPath(), is("target/thucydides"));
    }
    
    @Test
    public void the_output_directory_can_be_defined_by_a_system_property() throws InitializationError {

        WebDriverFactory mockBrowserFactory = mock(WebDriverFactory.class);
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);
        
        System.setProperty("thucydides.outputDirectory", "target/reports/thucydides");

        File outputDirectory = runner.getOutputDirectory();
        
        assertThat(outputDirectory.getPath(), is("target/reports/thucydides"));

    }

    private ThucydidesRunner getTestRunnerUsing(WebDriverFactory browserFactory) throws InitializationError {
        return new MockThucydidesRunner(ManagedWebDriverSample.class, browserFactory);   
    }
    
}
