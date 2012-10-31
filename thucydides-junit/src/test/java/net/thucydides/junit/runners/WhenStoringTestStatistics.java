package net.thucydides.junit.runners;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import net.thucydides.core.webdriver.ThucydidesWebdriverManager;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverInstanceFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import net.thucydides.junit.rules.DisableThucydidesHistoryRule;
import net.thucydides.junit.rules.QuietThucydidesLoggingRule;
import net.thucydides.samples.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

public class WhenStoringTestStatistics extends AbstractTestStepRunnerTest {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    WebdriverInstanceFactory webdriverInstanceFactory;

    @Mock
    FirefoxDriver firefoxDriver;

    EnvironmentVariables environmentVariables;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    WebDriverFactory webDriverFactory;

    @Before
    public void createATestableDriverFactory() throws Exception {
        MockitoAnnotations.initMocks(this);

        webdriverInstanceFactory = new WebdriverInstanceFactory() {

            @Override
            public WebDriver newFirefoxDriver(FirefoxProfile profile) {
                return firefoxDriver;
            }

        };

        environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables);
        StepEventBus.getEventBus().clear();

    }

    @Test
    public void the_test_runner_records_test_results_in_the_statistics_database() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(SamplePassingScenario.class, webDriverFactory);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(3));

        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("happy_day_scenario").getTitle(), is("Happy day scenario"));
        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("happy_day_scenario").getTestSteps().size(), is(4));

        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("edge_case_1").getTitle(), is("Edge case 1"));
        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("edge_case_1").getTestSteps().size(), is(3));

        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("edge_case_2").getTitle(), is("Edge case 2"));
        assertThat(inTheTesOutcomes(executedSteps).theOutcomeFor("edge_case_2").getTestSteps().size(), is(2));
    }

}