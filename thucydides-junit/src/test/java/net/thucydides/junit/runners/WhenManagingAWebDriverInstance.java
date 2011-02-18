package net.thucydides.junit.runners;

import static net.thucydides.core.webdriver.SupportedWebDriver.CHROME;
import static net.thucydides.core.webdriver.SupportedWebDriver.FIREFOX;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.webdriver.UnsupportedDriverException;
import net.thucydides.junit.annotations.InvalidManagedWebDriverFieldException;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.junit.runners.samples.WebDriverWithoutAnnotationSample;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

/**
 * Managing the WebDriver instance during a test run
 * The instance should be created once at the start of the test run,
 * and closed once at the end of the tets.
 * 
 * @author johnsmart
 * 
 */
public class WhenManagingAWebDriverInstance extends AbstractWebDriverTest {

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    @Test
    public void the_driver_should_be_initialized_before_the_tests() throws InitializationError  {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);
        
        final RunNotifier notifier = new RunNotifier();
        runner.run(notifier);
        
        assertThat(mockBrowserFactory.createdFirefoxDrivers(), is(1));
    }

    @Test
    public void the_driver_should_be_closed_after_the_tests() throws InitializationError {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(mockBrowserFactory);
        
        final RunNotifier notifier = new RunNotifier();
        runner.run(notifier);
        verify(mockBrowserFactory.getFirefoxDriver(), times(1)).quit();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void when_an_unsupported_driver_is_used_no_driver_is_created() throws InitializationError {

        System.setProperty("webdriver.driver", "htmlunit");      
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = null;
        try {
            runner = getTestRunnerUsing(mockBrowserFactory);
            final RunNotifier notifier = new RunNotifier();
            runner.run(notifier);
        } catch (UnsupportedDriverException e) {
            assertThat(e.getMessage(), allOf(containsString("htmlunit is not a supported browser"),
                                             containsString("Supported driver values are: "),
                                             containsString(FIREFOX.toString()),
                                             containsString(CHROME.toString())
                                             ));
        }
        
        assertThat(mockBrowserFactory.getFirefoxDriver(), is(nullValue()));
    }
    
    @Test(expected=InvalidManagedWebDriverFieldException.class)
    public void when_no_annotated_field_is_found_an_exception_is_thrown() throws InitializationError {

        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(WebDriverWithoutAnnotationSample.class, mockBrowserFactory);
        
        runner.run(new RunNotifier());
    }    
}
