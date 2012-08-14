package net.thucydides.junit.runners;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import net.thucydides.core.Thucydides;
import net.thucydides.core.annotations.ManagedWebDriverAnnotatedField;
import net.thucydides.core.annotations.Pending;
import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.core.batches.BatchManager;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.guice.ThucydidesModule;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.ReportService;
import net.thucydides.core.steps.StepAnnotations;
import net.thucydides.core.steps.StepData;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.ThucydidesWebdriverManager;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import net.thucydides.junit.listeners.JUnitStepListener;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static net.thucydides.core.Thucydides.initializeTestSession;

/**
 * A test runner for WebDriver-based web tests. This test runner initializes a
 * WebDriver instance before running the tests in their order of appearance. At
 * the end of the tests, it closes and quits the WebDriver instance.
 * <p/>
 * The test runner will by default produce output in XML and HTML. This
 * can extended by subscribing more reporter implementations to the test runner.
 *
 * @author johnsmart
 */
public class ThucydidesRunner extends BlockJUnit4ClassRunner {

    /**
     * Provides a proxy of the ScenarioSteps object used to invoke the test steps.
     * This proxy notifies the test runner about individual step outcomes.
     */
    private StepFactory stepFactory;
    private Pages pages;
    private final WebdriverManager webdriverManager;
    private String requestedDriver;
    private ReportService reportService;
    /**
     * Special listener that keeps track of test step execution and results.
     */
    private JUnitStepListener stepListener;
    /**
     * Retrieve the runner getConfiguration().from an external source.
     */
    private Configuration configuration;
    private boolean uniqueSession;

    private BatchManager batchManager;

    private final Logger logger = LoggerFactory.getLogger(ThucydidesRunner.class);

    public Pages getPages() {
        return pages;
    }

    /**
     * Creates a new test runner for WebDriver web tests.
     *
     * @throws InitializationError if some JUnit-related initialization problem occurred
     */
    public ThucydidesRunner(final Class<?> klass) throws InitializationError {
        this(klass, Injectors.getInjector());
    }

    public ThucydidesRunner(final Class<?> klass,
                            final Injector injector) throws InitializationError {
        this(klass,
            injector.getInstance(WebdriverManager.class),
            injector.getInstance(Configuration.class),
            injector.getInstance(BatchManager.class));
    }

    public ThucydidesRunner(final Class<?> klass,
                            final WebDriverFactory webDriverFactory) throws InitializationError {
        this(klass, webDriverFactory, Injectors.getInjector().getInstance(Configuration.class));
    }

    public ThucydidesRunner(final Class<?> klass,
                            final WebDriverFactory webDriverFactory,
                            final Configuration configuration) throws InitializationError {
        this(klass,
                new ThucydidesWebdriverManager(webDriverFactory, configuration),
                configuration,
                Injectors.getInjector().getInstance(BatchManager.class));

    }

    public ThucydidesRunner(final Class<?> klass, final BatchManager batchManager) throws InitializationError {
        this(klass,
                Injectors.getInjector().getInstance(WebdriverManager.class),
                Injectors.getInjector().getInstance(Configuration.class),
                batchManager);
    }

    public ThucydidesRunner(final Class<?> klass,
                            final WebdriverManager webDriverManager,
                            final Configuration configuration,
                            final BatchManager batchManager) throws InitializationError {
        super(klass);
        this.webdriverManager = webDriverManager;
        this.configuration = configuration;
        this.requestedDriver = getSpecifiedDriver(klass);

        if (TestCaseAnnotations.supportsWebTests(klass)) {
            checkRequestedDriverType();
        }

        this.batchManager = batchManager;

        batchManager.registerTestCase(klass);

        loadLocalPreferences();

    }

    private void loadLocalPreferences() throws InitializationError {
        try {
            Thucydides.loadLocalPreferences();
        } catch (IOException e) {
            throw new InitializationError(e);
        }
    }

    private String getSpecifiedDriver(Class<?> klass) {
        if (ManagedWebDriverAnnotatedField.hasManagedWebdriverField(klass)) {
            return ManagedWebDriverAnnotatedField.findFirstAnnotatedField(klass).getDriver();
        } else {
            return null;
        }
    }

    /**
     * The getConfiguration().manages output directories and driver types.
     * They can be defined as system values, or have sensible defaults.
     */
    protected Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Ensure that the requested driver type is valid before we start the tests.
     * Otherwise, throw an InitializationError.
     */
    private void checkRequestedDriverType() {
        if (requestedDriverSpecified()) {
            SupportedWebDriver.getDriverTypeFor(requestedDriver);
        } else {
            getConfiguration().getDriverType();
        }
    }

    private boolean requestedDriverSpecified() {
        return !StringUtils.isEmpty(this.requestedDriver);
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
        getReportService().subscribe(reporter);
    }

    public void useQualifier(final String qualifier) {
        getReportService().useQualifier(qualifier);
    }

    /**
     * Runs the tests in the acceptance test case.
     */

    @Override
    public void run(final RunNotifier notifier) {
        if (!skipThisTest()) {
            try {
                initializeDriversAndListeners(notifier);
                super.run(notifier);
            } finally {
                notifyTestSuiteFinished();
                generateReports();
                dropListeners(notifier);
                closeDrivers();
            }
        }
    }

    private void notifyTestSuiteFinished() {
        try {
            StepEventBus.getEventBus().testSuiteFinished();
        } catch (Throwable listenerException) {
            // We report and ignore listener exceptions so as not to mess up the rest of the test mechanics.
            logger.error("Test event bus error: " + listenerException.getMessage(), listenerException);
        }
    }

    private void dropListeners(final RunNotifier notifier) {
        JUnitStepListener listener = getStepListener();
        notifier.removeListener(listener);
        getStepListener().dropListeners();
    }

    private void generateReports() {
            generateReportsFor(getStepListener().getTestOutcomes());
    }

    private boolean skipThisTest() {
        return (batchManager != null) && (!batchManager.shouldExecuteThisTest());
    }

    /**
     * The Step Listener observes and records what happens during the execution of the test.
     * Once the test is over, the Step Listener can provide the acceptance test outcome in the
     * form of an TestOutcome object.
     */
    protected JUnitStepListener getStepListener() {
        if (stepListener == null) {
            buildAndConfigureListeners();
        }
        return stepListener;
    }

    protected void setStepListener(JUnitStepListener stepListener) {
        this.stepListener = stepListener;
    }

    private void buildAndConfigureListeners() {

        initStepEventBus();
        if (webtestsAreSupported()) {
            initPagesObjectUsing(webdriverManager.getWebdriver(requestedDriver));
            setStepListener(initListenersUsing(getPages()));
            initStepFactoryUsing(getPages());
        } else {
            setStepListener(initListeners());
            initStepFactory();
        }
    }

    private void initializeDriversAndListeners(RunNotifier notifier) {
        JUnitStepListener listener = getStepListener();
        notifier.addListener(listener);
    }

    private void initStepEventBus() {
        StepEventBus.getEventBus().clear();
    }

    private void initPagesObjectUsing(final WebDriver driver) {
        pages = new Pages(driver, getConfiguration());
    }

    protected JUnitStepListener initListenersUsing(final Pages pageFactory) {

        return JUnitStepListener.withOutputDirectory(getConfiguration().loadOutputDirectoryFromSystemProperties())
                                 .and().withPageFactory(pageFactory)
                                 .and().build();
    }

    protected JUnitStepListener initListeners() {

        return JUnitStepListener.withOutputDirectory(getConfiguration().loadOutputDirectoryFromSystemProperties())
                                                                       .and().build();
    }

    private boolean webtestsAreSupported() {
        return TestCaseAnnotations.supportsWebTests(this.getTestClass().getJavaClass());
    }

    private void initStepFactoryUsing(final Pages pagesObject) {
        stepFactory = new StepFactory(pagesObject);
    }

    private void initStepFactory() {
        stepFactory = new StepFactory();
    }

    private void closeDrivers() {
        getWebdriverManager().closeAllDrivers();
    }

    protected WebdriverManager getWebdriverManager() {
        return webdriverManager;
    }

    private ReportService getReportService() {
        if (reportService == null) {
            reportService = new ReportService(getOutputDirectory(), getDefaultReporters());
        }
        return reportService;
//        return Injectors.getInjector().getInstance(ReportService.class);
    }

    /**
     * A test runner can generate reports via Reporter instances that subscribe
     * to the test runner. The test runner tells the reporter what directory to
     * place the reports in. Then, at the end of the test, the test runner
     * notifies these reporters of the test outcomes. The reporter's job is to
     * process each test run outcome and do whatever is appropriate.
     * @param testOutcomeResults the test results from the previous test run.
     */
    private void generateReportsFor(final List<TestOutcome> testOutcomeResults) {
        getReportService().generateReportsFor(testOutcomeResults);
    }


    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        initializeTestSession();
        resetBroswerFromTimeToTime();
        if (isPending(method)) {
            markAsPending(method, notifier);
        } else {
            processTestMethodAnnotationsFor(method, notifier);
            super.runChild(method, notifier);
        }
    }

    private void markAsPending(FrameworkMethod method, RunNotifier notifier) {
        stepListener.testStarted(Description.createTestDescription(method.getMethod().getDeclaringClass(), method.getName()));
        StepEventBus.getEventBus().testPending();
        notifier.fireTestIgnored(Description.createTestDescription(method.getMethod().getDeclaringClass(), method.getName()));
    }

    /**
     * Process any Thucydides annotations in the test class.
     * Ignored tests will just be skipped by JUnit - we need to ensure
     * that they are included in the Thucydides reports
     * If a test method is pending, all the steps should be skipped.
     */
    private void processTestMethodAnnotationsFor(FrameworkMethod method, RunNotifier notifier) {
        if (isIgnored(method)) {
            stepListener.testStarted(Description.createTestDescription(method.getMethod().getDeclaringClass(), method.getName()));
            StepEventBus.getEventBus().testIgnored();
        }
    }


    private boolean isPending(FrameworkMethod method) {
        return method.getAnnotation(Pending.class) != null;
    }

    private boolean isIgnored(FrameworkMethod method) {
        return method.getAnnotation(Ignore.class) != null;
    }

    protected boolean restartBrowserBeforeTest() {
        return !uniqueSession;
    }

    protected boolean isUniqueSession() {
        return uniqueSession;
    }

    protected void resetBroswerFromTimeToTime() {
        if (restartBrowserBeforeTest()) {
            WebdriverProxyFactory.resetDriver(getDriver());
        }
    }

    /**
     * Running a unit test, which represents a test scenario.
     */
    @Override
    protected Statement methodInvoker(final FrameworkMethod method, final Object test) {

        if (webtestsAreSupported()) {
            injectDriverInto(test, method);
            initPagesObjectUsing(driverFor(method));
            injectAnnotatedPagesObjectInto(test);
            initStepFactoryUsing(getPages());
            uniqueSession = TestCaseAnnotations.forTestCase(test).isUniqueSession();

        }

        injectScenarioStepsInto(test);

        useStepFactoryForDataDrivenSteps();

        Statement baseStatement = super.methodInvoker(method, test);
        return new ThucydidesStatement(baseStatement, stepListener.getBaseStepListener());
    }

    private void useStepFactoryForDataDrivenSteps() {
        StepData.setDefaultStepFactory(stepFactory);
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    protected void injectDriverInto(final Object testCase,
                                    final FrameworkMethod method) {
        TestCaseAnnotations.forTestCase(testCase).injectDriver(driverFor(method));
    }

    protected WebDriver driverFor(final FrameworkMethod method) {
        if (TestMethodAnnotations.forTest(method).isDriverSpecified()) {
            String testSpecificDriver = TestMethodAnnotations.forTest(method).specifiedDriver();
            return getDriver(testSpecificDriver);
        } else {
            return getDriver();
        }

    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    protected void injectScenarioStepsInto(final Object testCase) {
        StepAnnotations.injectScenarioStepsInto(testCase, stepFactory);
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    protected void injectAnnotatedPagesObjectInto(final Object testCase) {
        StepAnnotations.injectAnnotatedPagesObjectInto(testCase, pages);
    }

    protected WebDriver getDriver() {
        return getWebdriverManager().getWebdriver(requestedDriver);
    }

    protected WebDriver getDriver(final String driver) {
        return getWebdriverManager().getWebdriver(driver);
    }

    public List<TestOutcome> getTestOutcomes() {
        return getStepListener().getTestOutcomes();
    }

    /**
     * The default reporters applicable for standard test runs.
     */
    protected Collection<AcceptanceTestReporter> getDefaultReporters() {
        return ReportService.getDefaultReporters();
    }


}
