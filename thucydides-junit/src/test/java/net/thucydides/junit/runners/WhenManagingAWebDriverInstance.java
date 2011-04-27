package net.thucydides.junit.runners;

import static net.thucydides.core.webdriver.SupportedWebDriver.CHROME;
import static net.thucydides.core.webdriver.SupportedWebDriver.FIREFOX;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.webdriver.UnsupportedDriverException;
import net.thucydides.junit.annotations.InvalidManagedWebDriverFieldException;
import net.thucydides.junit.runners.mocks.TestableWebDriverFactory;
import net.thucydides.samples.SampleFailingScenario;
import net.thucydides.samples.SamplePassingScenario;
import net.thucydides.samples.SampleScenarioWithUnannotatedWebDriver;

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
public class WhenManagingAWebDriverInstance extends AbstractTestStepRunnerTest {

    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    @Test
    public void the_driver_should_be_initialized_before_the_tests() throws InitializationError  {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(SampleFailingScenario.class, mockBrowserFactory);
        
        final RunNotifier notifier = new RunNotifier();
        runner.run(new RunNotifier());
        
        assertThat(mockBrowserFactory.createdFirefoxDrivers(), is(1));
    }

    @Test
    public void the_driver_should_be_closed_after_the_tests() throws InitializationError {
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(SampleFailingScenario.class, mockBrowserFactory);
        
        final RunNotifier notifier = new RunNotifier();
        runner.run(new RunNotifier());
        verify(mockBrowserFactory.getDriver(), times(1)).quit();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void when_an_unsupported_driver_is_used_an_error_is_raised() throws InitializationError {

        System.setProperty("webdriver.driver", "htmlunit");      
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = null;
        try {
            runner = getTestRunnerUsing(SampleFailingScenario.class, mockBrowserFactory);
            final RunNotifier notifier = new RunNotifier();
            runner.run(notifier);
            fail();
        } catch (UnsupportedDriverException e) {
            assertThat(e.getMessage(), allOf(containsString("htmlunit is not a supported browser"),
                                             containsString("Supported driver values are: "),
                                             containsString(FIREFOX.toString()),
                                             containsString(CHROME.toString())
                                             ));
        }
    }

    @Test
    public void a_system_provided_url_should_override_the_default_url() throws InitializationError {

        System.setProperty("webdriver.base.url", "http://www.wikipedia.com");
        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = null;
        runner = getTestRunnerUsing(SamplePassingScenario.class, mockBrowserFactory);
        final RunNotifier notifier = new RunNotifier();
        runner.run(notifier);

        verify(mockBrowserFactory.getDriver()).get("http://www.wikipedia.com");
    }
    
    @Test(expected=InvalidManagedWebDriverFieldException.class)
    public void when_no_annotated_field_is_found_an_exception_is_thrown() throws InitializationError {

        TestableWebDriverFactory mockBrowserFactory = new TestableWebDriverFactory();
        ThucydidesRunner runner = getTestRunnerUsing(SampleScenarioWithUnannotatedWebDriver.class, mockBrowserFactory);
        
        runner.run(new RunNotifier());
    }    
}
