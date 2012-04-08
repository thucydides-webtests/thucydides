package net.thucydides.junit.runners.integration;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.junit.rules.DisableThucydidesHistoryRule;
import net.thucydides.junit.rules.QuietThucydidesLoggingRule;
import net.thucydides.junit.runners.AbstractTestStepRunnerTest;
import net.thucydides.junit.runners.ThucydidesRunner;
import net.thucydides.samples.LongSamplePassingScenarioUsingFirefox;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenRunningATestScenarioWithScreenshots extends AbstractTestStepRunnerTest {

//    @Before
//    public void initMocks() {
//        MockitoAnnotations.initMocks(this);
//    }

//    WebdriverInstanceFactory webdriverInstanceFactory;
//
//    FirefoxDriver firefoxDriver;

    EnvironmentVariables environmentVariables;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public QuietThucydidesLoggingRule quietThucydidesLoggingRule = new QuietThucydidesLoggingRule();

    @Rule
    public DisableThucydidesHistoryRule disableThucydidesHistoryRule = new DisableThucydidesHistoryRule();

//    WebDriverFactory webDriverFactory;
//
//    @Before
//    public void createATestableDriverFactory() throws Exception {
//
//        MockitoAnnotations.initMocks(this);
//
//        webdriverInstanceFactory = new WebdriverInstanceFactory() {
//            @Override
//            public WebDriver newInstanceOf(Class<? extends WebDriver> webdriverClass, FirefoxProfile profile) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
//                return firefoxDriver;
//            }
//
//            @Override
//            public WebDriver newInstanceOf(Class<? extends WebDriver> webdriverClass) throws IllegalAccessException, InstantiationException {
//                return firefoxDriver;
//            }
//        };
//
//        environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
//        webDriverFactory = new WebDriverFactory(webdriverInstanceFactory, environmentVariables);
//        StepEventBus.getEventBus().clear();
//
//    }

    @Test
    public void the_test_runner_records_the_steps_as_they_are_executed() throws InitializationError {

        ThucydidesRunner runner = new ThucydidesRunner(LongSamplePassingScenarioUsingFirefox.class);
        runner.run(new RunNotifier());

        List<TestOutcome> executedSteps = runner.getTestOutcomes();
        assertThat(executedSteps.size(), is(1));
        TestOutcome testOutcome1 = executedSteps.get(0);

        assertThat(testOutcome1.getTitle(), is("Happy day scenario"));
        assertThat(testOutcome1.getMethodName(), is("happy_day_scenario"));
        assertThat(testOutcome1.getTestSteps().size(), is(3));
        assertThat(testOutcome1.getScreenshots().size(), is(5));
    }

}