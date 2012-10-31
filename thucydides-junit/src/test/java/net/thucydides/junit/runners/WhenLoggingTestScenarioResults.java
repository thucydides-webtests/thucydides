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

import static net.thucydides.junit.util.FileFormating.md5;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenLoggingTestScenarioResults extends AbstractTestStepRunnerTest {

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

    @Rule
    public DisableThucydidesHistoryRule disableThucydidesHistoryRule = new DisableThucydidesHistoryRule();

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
    public void the_test_runner_records_the_name_of_the_test_scenario() throws InitializationError {

        ThucydidesRunner runner1 = new ThucydidesRunner(SingleNonWebTestScenario.class);
        ThucydidesRunner runner2 = new ThucydidesRunner(MultipleNonWebTestScenario.class);
        ThucydidesRunner runner3 = new ThucydidesRunner(MultipleNonWebTestScenario.class);
        runner1.run(new RunNotifier());
        runner2.run(new RunNotifier());
        runner3.run(new RunNotifier());
    }


}