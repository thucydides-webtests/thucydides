package net.thucydides.junit.runners;

import net.thucydides.core.annotations.InvalidManagedWebDriverFieldException;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.webdriver.UnsupportedDriverException;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverInstanceFactory;
import net.thucydides.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.samples.MultipleTestScenario;
import net.thucydides.samples.MultipleTestScenarioWithUniqueSession;
import net.thucydides.samples.SamplePassingScenario;
import net.thucydides.samples.SampleScenarioWithUnannotatedWebDriver;
import net.thucydides.samples.SingleTestScenario;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.lang.reflect.InvocationTargetException;

import static net.thucydides.core.webdriver.SupportedWebDriver.CHROME;
import static net.thucydides.core.webdriver.SupportedWebDriver.FIREFOX;
import static net.thucydides.core.webdriver.SupportedWebDriver.IEXPLORER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    WebdriverInstanceFactory webdriverInstanceFactory;

    @Mock
    FirefoxDriver firefoxDriver;

    WebDriverFactory webDriverFactory;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);

        webdriverInstanceFactory = new WebdriverInstanceFactory() {

            @Override
            public WebDriver newInstanceOf(Class<? extends WebDriver> webdriverClass, FirefoxProfile profile) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
                return firefoxDriver;
            }
        };

        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory);

        StepEventBus.getEventBus().clear();

    }


    @Test
    public void the_driver_should_be_initialized_before_the_tests() throws InitializationError  {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class, webDriverFactory);

        runner.run(new RunNotifier());

        assertThat(firefoxDriver, is(notNullValue()));
    }

    @Test
    public void the_driver_should_be_reset_after_each_test() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(MultipleTestScenario.class, webDriverFactory);

        runner.run(new RunNotifier());

        verify(firefoxDriver,times(3)).quit();
    }

    @Test
    public void the_driver_should_only_be_reset_once_at_the_start_for_unique_session_tests() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(MultipleTestScenarioWithUniqueSession.class, webDriverFactory);

        runner.run(new RunNotifier());

        verify(firefoxDriver,times(1)).quit();
    }


    @Test
    public void the_driver_should_be_closed_after_the_tests() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class, webDriverFactory);
        
        runner.run(new RunNotifier());
        verify(firefoxDriver).close();
    }

    @Test
    public void when_an_unsupported_driver_is_used_an_error_is_raised() throws InitializationError {

        System.setProperty("webdriver.driver", "htmlunit");      
        try {
            ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class, webDriverFactory);
            runner.run(new RunNotifier());
            fail();
        } catch (UnsupportedDriverException e) {
            assertThat(e.getMessage(), allOf(containsString("htmlunit is not a supported browser"),
                                             containsString("Supported driver values are: "),
                                             containsString(FIREFOX.toString()),
                                                containsString(CHROME.toString()),
                                                containsString(IEXPLORER.toString())
                                             ));
        }
    }

    @Test
    public void a_system_provided_url_should_override_the_default_url() throws InitializationError {

        System.setProperty("webdriver.base.url", "http://www.wikipedia.com");
        ThucydidesRunner runner = new ThucydidesRunner(SingleTestScenario.class, webDriverFactory);

        runner.run(new RunNotifier());

        verify(firefoxDriver).get("http://www.wikipedia.com");
    }
}
