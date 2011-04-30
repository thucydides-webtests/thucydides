package net.thucydides.junit.runners;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.reports.html.HtmlAcceptanceTestReporter;
import net.thucydides.core.reports.xml.XMLAcceptanceTestReporter;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import net.thucydides.junit.steps.ScenarioStepListener;
import net.thucydides.junit.steps.StepAnnotations;
import net.thucydides.junit.steps.StepFactory;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * A test runner for WebDriver-based web tests. This test runner initializes a
 * WebDriver instance before running the tests in their order of appearance. At
 * the end of the tests, it closes and quits the WebDriver instance.
 * 
 * The test runner will by default produce output in XML and HTML. This
 * can extended by subscribing more reporter implementations to the test runner.
 * 
 * 
 * @author johnsmart
 * 
 */
public class ThucydidesRunner extends BlockJUnit4ClassRunner {

    /**
     * Creates new browser instances. The Browser Factory's job is to provide
     * new web driver instances. It is designed to isolate the test runner from
     * the business of creating and managing WebDriver drivers.
     */
    private WebDriverFactory webDriverFactory;

    /**
     * Provides a proxy of the ScenarioSteps object used to invoke the test steps.
     * This proxy notifies the test runner about individual step outcomes.
     */
    private StepFactory stepFactory;
    
    private Pages pages;
 
    private WebdriverManager webdriverManager;
    
    /**
     * Special listener that keeps track of test step execution and results.
     */
    private ScenarioStepListener stepListener;  
    
    /**
     * Retrieve the runner configuration from an external source.
     */
    private Configuration configuration;

    private static final Logger LOGGER = LoggerFactory.getLogger(ThucydidesRunner.class);

    private ReportService reportService;

    /**
     * The Step Listener observes and records what happens during the execution of the test.
     * Once the test is over, the Step Listener can provide the acceptance test outcome in the
     * form of an AcceptanceTestRun object.
     */
    public ScenarioStepListener getStepListener() {
        if (stepListener == null) {
            stepListener = new ScenarioStepListener((TakesScreenshot) getDriver(), getConfiguration());
        }
        return stepListener;
    }

    /**
     * Override the default step listener. Mainly for testing.
     */
    protected void setStepListener(final ScenarioStepListener stepListener) {
        this.stepListener = stepListener;
    }

    /**
     * Creates a new test runner for WebDriver web tests.
     * 
     * @throws InitializationError
     *             if some JUnit-related initialization problem occurred
     */
    public ThucydidesRunner(final Class<?> klass) throws InitializationError {
        super(klass);
        reportService = new ReportService(getConfiguration().getOutputDirectory(),
                                          getDefaultReporters());
        checkRequestedDriverType();
        TestCaseAnnotations.checkThatTestCaseIsCorrectlyAnnotated(klass);

        webDriverFactory = new WebDriverFactory();
    }

    /**
     * The configuration manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     */
    protected Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    /**
     * Set the configuration for a test runner.
     * @param configuration
     */
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }
    

    /**
     * Ensure that the requested driver type is valid before we start the tests.
     * Otherwise, throw an InitializationError.
     */
    private void checkRequestedDriverType() {
        Configuration.getDriverType();
    }

    /**
     * Override the default web driver factory. Normal users shouldn't need to
     * do this very often.
     */
    public void setWebDriverFactory(final WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    public File getOutputDirectory() {
        return getConfiguration().getOutputDirectory();
    }

    
    /**
     * To generate reports, different AcceptanceTestReporter instances need to
     * subscribe to the listener. The listener will tell them when the test is
     * done, and the reporter can decide what to do.
     */
    public void subscribeReporter(final AcceptanceTestReporter reporter) {
        reportService.subscribe(reporter);
    }

    public void useQualifier(final String qualifier) {
        reportService.useQualifier(qualifier);
    }
    /**
     * Runs the tests in the acceptance test case.
     */
    @Override
    public void run(final RunNotifier notifier) {
        initWebdriverManager();

        super.run(notifier);

        webdriverManager.closeDriver();
        
        generateReportsFor(getStepListener().getTestRunResults());

        notifyFailures();
    }

    protected void initWebdriverManager() {
        webdriverManager = new WebdriverManager(webDriverFactory);
    }

    private void notifyFailures() {
        if (stepFactory != null) {
            stepFactory.notifyStepFailures();
        }
    }

    /**
     * A test runner can generate reports via Reporter instances that subscribe
     * to the test runner. The test runner tells the reporter what directory to
     * place the reports in. Then, at the end of the test, the test runner
     * notifies these reporters of the test outcomes. The reporter's job is to
     * process each test run outcome and do whatever is appropriate.
     */
    private void generateReportsFor(final List<AcceptanceTestRun> testRunResults) {
        reportService.generateReportsFor(testRunResults);
    }

    /**
     * Running a unit test, which represents a test scenario.
     */
    @Override
    protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
        
        injectDriverInto(test);
        injectAnnotatedPagesObjectInto(test);
        injectScenarioStepsInto(test);
        stepFactory.addListener(getStepListener());
        
        notifyTestStart(method);
        
        return super.methodInvoker(method, test);
    }

    
    private void notifyTestStart(final FrameworkMethod method) {
        try {
            getStepListener().testRunStarted(Description.createTestDescription(method.getMethod().getDeclaringClass(),
                                                                          method.getName()));
        } catch (Exception e) {
            LOGGER.error("Failed to start test run", e);
        }
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    protected void injectDriverInto(final Object testCase) {
        TestCaseAnnotations.injectDriverInto(testCase, getDriver());
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    protected void injectScenarioStepsInto(final Object testCase) {
       stepFactory = new StepFactory(pages);
       StepAnnotations.injectScenarioStepsInto(testCase, stepFactory);
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    protected void injectAnnotatedPagesObjectInto(final Object testCase) {
       pages = new Pages(getDriver());
       StepAnnotations.injectAnnotatedPagesObjectInto(testCase, pages); 
    }

    protected WebDriver getDriver() {
        return webdriverManager.getWebdriver();
    }

    public List<AcceptanceTestRun> getAcceptanceTestRuns() {
        return getStepListener().getTestRunResults();
    }

    /**
     * The default reporters applicable for standard test runs.
     */
    protected Collection<AcceptanceTestReporter> getDefaultReporters() {
        return ImmutableList.of(new XMLAcceptanceTestReporter(),
                new HtmlAcceptanceTestReporter());
    }
}
