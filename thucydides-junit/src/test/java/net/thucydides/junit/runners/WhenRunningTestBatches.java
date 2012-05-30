package net.thucydides.junit.runners;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.ThucydidesWebdriverManager;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverInstanceFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import net.thucydides.junit.rules.QuietThucydidesLoggingRule;
import net.thucydides.samples.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WhenRunningTestBatches extends AbstractTestStepRunnerTest {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    WebdriverInstanceFactory webdriverInstanceFactory;

    @Mock
    FirefoxDriver firefoxDriver;

    MockEnvironmentVariables environmentVariables;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public QuietThucydidesLoggingRule quietThucydidesLoggingRule = new QuietThucydidesLoggingRule();

    WebDriverFactory webDriverFactory;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);

        webdriverInstanceFactory = new WebdriverInstanceFactory() {
            @Override
            public WebDriver newInstanceOf(Class<? extends WebDriver> webdriverClass, FirefoxProfile profile) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
                return firefoxDriver;
            }

            @Override
            public WebDriver newInstanceOf(Class<? extends WebDriver> webdriverClass) throws IllegalAccessException, InstantiationException {
                return firefoxDriver;
            }
        };

        environmentVariables = new MockEnvironmentVariables();
        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables);
        StepEventBus.getEventBus().clear();

    }

    @Test
    public void the_test_runner_records_the_steps_as_they_are_executed() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class, webDriverFactory);

        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));
        TestOutcome testOutcome1 = executedSteps.get(0);
        TestOutcome testOutcome2 = executedSteps.get(1);
        TestOutcome testOutcome3 = executedSteps.get(2);

        assertThat(testOutcome1.getTitle(), is("Happy day scenario"));
        assertThat(testOutcome1.getMethodName(), is("happy_day_scenario"));
        assertThat(testOutcome1.getTestSteps().size(), is(4));

        assertThat(testOutcome2.getTitle(), is("Edge case 1"));
        assertThat(testOutcome2.getMethodName(), is("edge_case_1"));
        assertThat(testOutcome2.getTestSteps().size(), is(3));

        assertThat(testOutcome3.getTitle(), is("Edge case 2"));
        assertThat(testOutcome3.getMethodName(), is("edge_case_2"));
        assertThat(testOutcome3.getTestSteps().size(), is(2));
    }

}