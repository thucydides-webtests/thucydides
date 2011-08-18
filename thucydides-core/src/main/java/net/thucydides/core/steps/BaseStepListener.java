package net.thucydides.core.steps;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;
import static net.thucydides.core.util.NameConverter.underscore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.screenshots.ScreenshotException;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebdriverProxyFactory;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Observes the test run and stores test run details for later reporting.
 * Observations are recorded in an TestOutcome object. This includes
 * recording the names and results of each test, and taking and storing
 * screenshots at strategic points during the tests.
 */
public class BaseStepListener implements StepListener {

    /**
     * Used to build the test outcome structure as the test step results come in.
     */
    private final List<TestOutcome> testOutcomes;

    /**
     * Keeps track of what steps have been started but not finished, in order to structure nested steps.
     */
    private final Stack<TestStep> currentTestStack;

    /**
     * Keeps track of the current step group, if any.
     */
    private final Stack<TestStep> currentGroupStack;

    /**
     * Clock used to pause test execution.
     */
    private final SystemClock clock;

    /**
     * The Java class (if any) containing the tests.
     */
    private Class<?> testSuite;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStepListener.class);

    private WebDriver driver;

    private File outputDirectory;

    private WebdriverProxyFactory proxyFactory;

    private BaseStepListener(final File outputDirectory) {
        this.proxyFactory = WebdriverProxyFactory.getFactory();
        this.testOutcomes = new ArrayList<TestOutcome>();
        this.currentTestStack = new Stack<TestStep>();
        this.currentGroupStack = new Stack<TestStep>();
        this.outputDirectory = outputDirectory;
        this.clock = Injectors.getInjector().getInstance(SystemClock.class);
    }

    /**
     * Create a step listener with a given web driver type.
     * @param driverClass a driver of this type will be used
     * @param outputDirectory reports and screenshots are generated here
     */
    public BaseStepListener(final Class<? extends WebDriver> driverClass, final File outputDirectory) {
        this(outputDirectory);
        this.driver = getProxyFactory().proxyFor(driverClass);
    }

    /**
     * Create a step listener using the driver from a given page factory.
     * If the pages factory is null, a new driver will be created based on the default system values.
     * @param outputDirectory reports and screenshots are generated here
     * @param pages a pages factory.
     */
    public BaseStepListener(final File outputDirectory, final Pages pages) {
         this(outputDirectory);
         if (pages != null) {
             setDriverUsingPagesDriverIfDefined(pages);
         } else {
             createNewDriver();
         }
    }

    private void createNewDriver() {
        setDriver(getProxyFactory().proxyDriver());
    }

    private void setDriverUsingPagesDriverIfDefined(final Pages pages) {
        if (pages.getDriver() != null) {
            setDriver(pages.getDriver());
        } else {
            createNewDriver();
            pages.setDriver(getDriver());
            pages.notifyWhenDriverOpens();
        }
    }

    protected WebdriverProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    protected TestOutcome getCurrentTestOutcome() {
        Preconditions.checkState(!testOutcomes.isEmpty());
        return testOutcomes.get(testOutcomes.size() - 1);
    }

    protected SystemClock getClock() {
        return clock;
    }

    /**
     * A test suite (containing a series of tests) starts.
     * @param startedTestSuite the class implementing the test suite (e.g. a JUnit test case)
     */
    public void testSuiteStarted(final Class<?> startedTestSuite) {
        LOGGER.debug("testSuiteStarted for " + startedTestSuite);
        testSuite = startedTestSuite;
    }

    /**
     * An individual test starts.
     * @param testMethod the name of the test method in the test suite class.
     */
    public void testStarted(final String testMethod) {
        LOGGER.debug("test started: " + testMethod);
        testOutcomes.add(TestOutcome.forTest(testMethod, testSuite));
        setAnnotatedResult(testMethod);
    }

    private void setAnnotatedResult(String testMethod) {
        if (TestAnnotations.forClass(testSuite).isIgnored(testMethod)) {
            getCurrentTestOutcome().setAnnotatedResult(IGNORED);
        }
        if (TestAnnotations.forClass(testSuite).isPending(testMethod)) {
            getCurrentTestOutcome().setAnnotatedResult(PENDING);
        }
    }

    /**
     * A test has finished.
     * @param result the summary of the test run, including all the tests that failed
     */
    public void testFinished(final TestStepResult result) {
        System.out.println("test finished: " + result);
        currentTestStack.clear();
    }

    /**
     * A step within a test is called.
     * This step might be nested in another step, in which case the original step becomes a group of steps.
     * @param description the description of the test that is about to be run
     */
    public void stepStarted(final ExecutedStepDescription description) {
        LOGGER.debug("step started: " + description);
        String stepName = AnnotatedStepDescription.from(description).getName();
        TestStep step = new TestStep(stepName);

        startNewGroupIfNested();
        setDefaultResultFromAnnotations(step, description);

        currentTestStack.push(step);

        getCurrentTestOutcome().recordStep(step);
    }

    private void setDefaultResultFromAnnotations(final TestStep step, final ExecutedStepDescription description) {
        if (TestAnnotations.isPending(description.getTestMethod())) {
            step.setResult(TestResult.PENDING);
        }
        if (TestAnnotations.isIgnored(description.getTestMethod())) {
            step.setResult(TestResult.SKIPPED);
        }
    }

    private void startNewGroupIfNested() {
        if (thereAreUnfinishedSteps()) {
            if (getCurrentStep() != getCurrentGroup()) {
                startNewGroup();
            }
        }
    }

    private void startNewGroup() {
        getCurrentTestOutcome().startGroup();
        currentGroupStack.push(getCurrentStep());
    }

    private TestStep getCurrentStep() {
        return currentTestStack.peek();
    }

    private TestStep getCurrentGroup() {
        if (currentGroupStack.isEmpty()) {
            return null;
        } else {
            return currentGroupStack.peek();
        }
    }

    private boolean thereAreUnfinishedSteps() {
        return !currentTestStack.isEmpty();
    }

    public void stepFinished(ExecutedStepDescription description) {
        LOGGER.debug("step finished: " + description);
        takeScreenshotFor(description, SUCCESS);
        currentStepDone();
        markCurrentStepAs(SUCCESS);
        pauseIfRequired();
    }

    private void finishGroup() {
        currentGroupStack.pop();
        getCurrentTestOutcome().endGroup();
    }

    private void pauseIfRequired() {
        int delay = Configuration.getStepDelay();
        if (delay > 0) {
            getClock().pauseFor(delay);
        }
    }

    private void markCurrentStepAs(final TestResult result) {
        getCurrentTestOutcome().getCurrentStep().setResult(result);
    }

    public void stepFailed(StepFailure failure) {
        LOGGER.debug("step failed: " + failure);
        takeScreenshotFor(failure.getDescription(), FAILURE);
        getCurrentTestOutcome().setTestFailureCause(failure.getException());
        markCurrentStepAs(FAILURE);
        recordFailureDetailsInFailingTestStep(failure);
        currentStepDone();
    }

    private void recordFailureDetailsInFailingTestStep(final StepFailure failure) {
        getCurrentStep().failedWith(failure.getMessage(), failure.getException());
    }

    public void stepIgnored(ExecutedStepDescription description) {
        LOGGER.debug("step ignored: " + description);
        if (TestAnnotations.isPending(description.getTestMethod())) {
            markCurrentStepAs(PENDING);
        } else if (aStepHasFailed()) {
            markCurrentStepAs(SKIPPED);
        } else {
            markCurrentStepAs(IGNORED);
        }
        currentStepDone();
    }

    public void stepPending() {
        LOGGER.debug("step pending");
        markCurrentStepAs(PENDING);
    }

    private void currentStepDone() {
        TestStep finishedStep =  currentTestStack.pop();

        if (finishedStep == getCurrentGroup()) {
            finishGroup();
        }

    }


    private void takeScreenshotFor(final ExecutedStepDescription description, TestResult result) {
        if ((getCurrentStep() != null) && (shouldTakeScreenshotFor(result))) {
            try {
                String testName = aTestCalled(description);
                File screenshot = grabScreenshotFileFor(testName);
                getCurrentStep().setScreenshot(screenshot);
                if (screenshot != null) {
                    File sourcecode = getPhotographer().getMatchingSourceCodeFor(screenshot);
                    getCurrentStep().setHtmlSource(sourcecode);
                }
            } catch (ScreenshotException e) {
                LOGGER.warn("Failed to take screenshot", e);
            }
        }
    }

    private File grabScreenshotFileFor(final String testName) {
        String snapshotName = underscore(testName);
        return getPhotographer().takeScreenshot(snapshotName);
    }

    public Photographer getPhotographer() {
        return new Photographer(driver, outputDirectory);

    }

    private boolean shouldTakeScreenshotFor(final TestResult result) {
        String onlySaveFailures = System.getProperty(ThucydidesSystemProperty.ONLY_SAVE_FAILING_SCREENSHOTS.getPropertyName(), "false");
        Boolean onlySaveFailureScreenshots = Boolean.valueOf(onlySaveFailures);
        return !(onlySaveFailureScreenshots && result != FAILURE);
    }

    protected String aTestCalled(final ExecutedStepDescription description) {
        return description.getName();
    }

    public List<TestOutcome> getTestOutcomes() {
        return ImmutableList.copyOf(testOutcomes);
    }


    public void setDriver(final WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public boolean aStepHasFailed() {
        return (!getTestOutcomes().isEmpty()) && (getCurrentTestOutcome().getTestFailureCause() != null);
    }

    public Throwable getTestFailureCause() {
        return getCurrentTestOutcome().getTestFailureCause();
    }

    public void testFailed(final Throwable cause) {
        getCurrentTestOutcome().setTestFailureCause(cause);
    }

    public void testIgnored() {
        getCurrentTestOutcome().setAnnotatedResult(IGNORED);
    }

}
