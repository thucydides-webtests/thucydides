package net.thucydides.core.steps;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.thucydides.core.IgnoredStepException;
import net.thucydides.core.PendingStepException;
import net.thucydides.core.Thucydides;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.*;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.screenshots.*;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static net.thucydides.core.model.Stories.findStoryFrom;
import static net.thucydides.core.model.TestResult.*;
import static net.thucydides.core.steps.BaseStepListener.ScreenshotType.MANDATORY_SCREENSHOT;
import static net.thucydides.core.steps.BaseStepListener.ScreenshotType.OPTIONAL_SCREENSHOT;
import static net.thucydides.core.util.NameConverter.underscore;
import static org.apache.commons.io.FileUtils.checksumCRC32;

/**
 * Observes the test run and stores test run details for later reporting.
 * Observations are recorded in an TestOutcome object. This includes
 * recording the names and results of each test, and taking and storing
 * screenshots at strategic points during the tests.
 */
public class BaseStepListener implements StepListener, StepPublisher {

    /**
     * Used to build the test outcome structure as the test step results come in.
     */
    private final List<TestOutcome> testOutcomes;

    /**
     * Keeps track of what steps have been started but not finished, in order to structure nested steps.
     */
    private final Stack<TestStep> currentStepStack;

    /**
     * Keeps track of the current step group, if any.
     */
    private final Stack<TestStep> currentGroupStack;

    /**
     * Clock used to pause test execution.
     */
    private final SystemClock clock;

    private ScreenshotPermission screenshots;
    /**
     * The Java class (if any) containing the tests.
     */
    private Class<?> testSuite;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStepListener.class);

    private WebDriver driver;

    private File outputDirectory;

    private WebdriverProxyFactory proxyFactory;

    private Story testedStory;

    private Configuration configuration;

    ScreenshotProcessor screenshotProcessor;

    private boolean inFluentStepSequence;

    private List<String> storywideIssues;

    private List<TestTag> storywideTags;

    protected enum ScreenshotType {
        OPTIONAL_SCREENSHOT,
        MANDATORY_SCREENSHOT
    }

    public BaseStepListener(final File outputDirectory) {
        this.proxyFactory = WebdriverProxyFactory.getFactory();
        this.testOutcomes = new ArrayList<TestOutcome>();
        this.currentStepStack = new Stack<TestStep>();
        this.currentGroupStack = new Stack<TestStep>();
        this.outputDirectory = outputDirectory;
        this.clock = Injectors.getInjector().getInstance(SystemClock.class);
        this.configuration = Injectors.getInjector().getInstance(Configuration.class);
        this.screenshotProcessor = Injectors.getInjector().getInstance(ScreenshotProcessor.class);
        this.inFluentStepSequence = false;
        this.storywideIssues = Lists.newArrayList();
        this.storywideTags = Lists.newArrayList();
    }

    protected ScreenshotPermission screenshots() {
        if (screenshots == null) {
            screenshots = new ScreenshotPermission(configuration);
        }
        return screenshots;
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

    public BaseStepListener(final Class<? extends WebDriver> driverClass,
                            final File outputDirectory,
                            final Configuration configuration) {
        this(outputDirectory);
        this.driver = getProxyFactory().proxyFor(driverClass);
        this.configuration = configuration;
    }

    public BaseStepListener(final File outputDirectory,
                            final Configuration configuration) {
        this(outputDirectory);
        this.driver = getProxyFactory().proxyFor(null);
        this.configuration = configuration;
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
        testSuite = startedTestSuite;
        testedStory = findStoryFrom(startedTestSuite);
        clearStorywideTagsAndIssues();
    }

    private void clearStorywideTagsAndIssues() {
        storywideIssues.clear();
        storywideTags.clear();
    }

    public void testSuiteStarted(final Story story) {
        testSuite = null;
        testedStory = story;
        clearStorywideTagsAndIssues();
    }

    public boolean testSuiteRunning() {
        return testedStory != null;
    }

    public void addIssuesToCurrentStory(List<String> issues) {
        storywideIssues.addAll(issues);
    }

    public void addTagsToCurrentStory(List<TestTag> tags) {
        storywideTags.addAll(tags);
    }

    public void testSuiteFinished() {
        screenshotProcessor.waitUntilDone();
        clearStorywideTagsAndIssues();
    }


    /**
     * An individual test starts.
     * @param testMethod the name of the test method in the test suite class.
     */
    public void testStarted(final String testMethod) {
        testOutcomes.add(TestOutcome.forTestInStory(testMethod, testSuite, testedStory));
        updateSessionIdIfKnown();
        setAnnotatedResult(testMethod);
    }

    private void updateSessionIdIfKnown() {
        String sessionId = Thucydides.getCurrentSessionID();
        if (sessionId != null) {
            getCurrentTestOutcome().setSessionId(sessionId);
        }
    }

    public void updateCurrentStepTitle(String updatedStepTitle) {
        getCurrentStep().setDescription(updatedStepTitle);
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
     * @param outcome the result of the test that just finished.
     */
    public void testFinished(final TestOutcome outcome) {
        recordTestDuration();
        getCurrentTestOutcome().addIssues(storywideIssues);
        getCurrentTestOutcome().addTags(storywideTags);
        currentStepStack.clear();
    }

    private void recordTestDuration() {
        if (!testOutcomes.isEmpty()) {
            getCurrentTestOutcome().recordDuration();
        }
    }

    /**
     * A step within a test is called.
     * This step might be nested in another step, in which case the original step becomes a group of steps.
     * @param description the description of the test that is about to be run
     */
    public void stepStarted(final ExecutedStepDescription description) {
        recordStep(description);
        takeInitialScreenshot();
        updateSessionIdIfKnown();
    }

    public void skippedStepStarted(final ExecutedStepDescription description) {
        recordStep(description);
    }

    private void recordStep(ExecutedStepDescription description) {
        String stepName = AnnotatedStepDescription.from(description).getName();

        updateFluentStepStatus(description, stepName);

        if (justStartedAFluentSequenceFor(description) || notInAFluentSequence()) {

            TestStep step = new TestStep(stepName);

            startNewGroupIfNested();
            setDefaultResultFromAnnotations(step, description);
    
            currentStepStack.push(step);
            recordStepToCurrentTestOutcome(step);
        }
        inFluentStepSequence = AnnotatedStepDescription.from(description).isFluent();
    }

    private void recordStepToCurrentTestOutcome(TestStep step) {
        getCurrentTestOutcome().recordStep(step);
    }

    private void updateFluentStepStatus(ExecutedStepDescription description, String stepName) {
        if (currentlyInAFluentSequenceFor(description) || justFinishedAFluentSequenceFor(description)) {
            addToFluentStepName(stepName);
        }
    }

    private void addToFluentStepName(String stepName) {
        String updatedStepName = getCurrentStep().getDescription() + " " + StringUtils.uncapitalize(stepName);
        getCurrentStep().setDescription(updatedStepName);
    }

    private boolean notInAFluentSequence() {
        return !inFluentStepSequence;
    }

    private boolean justFinishedAFluentSequenceFor(ExecutedStepDescription description) {
        boolean thisStepIsFluent = AnnotatedStepDescription.from(description).isFluent();
        return (inFluentStepSequence && !thisStepIsFluent);
    }

    private boolean justStartedAFluentSequenceFor(ExecutedStepDescription description) {
        boolean thisStepIsFluent = AnnotatedStepDescription.from(description).isFluent();
        return (!inFluentStepSequence && thisStepIsFluent);
    }

    private boolean currentlyInAFluentSequenceFor(ExecutedStepDescription description) {
        boolean thisStepIsFluent = AnnotatedStepDescription.from(description).isFluent();
        return (inFluentStepSequence && thisStepIsFluent);
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
        return currentStepStack.peek();
    }

    private Optional<TestStep> getPreviousStep() {
        if (currentStepStack.size() > 1) {
            return Optional.of(currentStepStack.get(currentStepStack.size() - 2));
        } else {
            return Optional.absent();
        }
    }

    private TestStep getCurrentGroup() {
        if (currentGroupStack.isEmpty()) {
            return null;
        } else {
            return currentGroupStack.peek();
        }
    }

    private boolean thereAreUnfinishedSteps() {
        return !currentStepStack.isEmpty();
    }

    public void stepFinished() {
        updateSessionIdIfKnown();
        takeEndOfStepScreenshotFor(SUCCESS);
        currentStepDone();
        markCurrentStepAs(SUCCESS);
        pauseIfRequired();
    }

    private void updateExampleTableIfNecessary(TestResult result) {
        if (getCurrentTestOutcome().isDataDriven()) {
            getCurrentTestOutcome().updateCurrentRowResult(result);
        }
    }

    private void finishGroup() {
        currentGroupStack.pop();
        getCurrentTestOutcome().endGroup();
    }

    private void pauseIfRequired() {
        int delay = configuration.getStepDelay();
        if (delay > 0) {
            getClock().pauseFor(delay);
        }
    }

    private void markCurrentStepAs(final TestResult result) {
        getCurrentTestOutcome().getCurrentStep().setResult(result);
        updateExampleTableIfNecessary(result);
    }

    public void stepFailed(StepFailure failure) {
        takeEndOfStepScreenshotFor(FAILURE);
        getCurrentTestOutcome().setTestFailureCause(failure.getException());
        markCurrentStepAs(FAILURE);
        recordFailureDetailsInFailingTestStep(failure);
        currentStepDone();
    }

    public void lastStepFailed(StepFailure failure) {
        takeEndOfStepScreenshotFor(FAILURE);
        getCurrentTestOutcome().lastStepFailedWith(failure);
    }


    private void recordFailureDetailsInFailingTestStep(final StepFailure failure) {
        if (currentStepExists()) {
            getCurrentStep().failedWith(new StepFailureException(failure.getMessage(), failure.getException()));
        }
    }

    public void stepIgnored() {
        if (aStepHasFailed()) {
            markCurrentStepAs(SKIPPED);
            currentStepDone();
        } else {
            markCurrentStepAs(IGNORED);
            currentStepDone();
        }
    }

    public void stepIgnored(String message) {
        getCurrentStep().testAborted(new IgnoredStepException(message));
        stepIgnored();
    }

    public void stepPending() {
        markCurrentStepAs(PENDING);
        currentStepDone();
    }

    public void stepPending(String message) {
        getCurrentStep().testAborted(new PendingStepException(message));
        stepPending();
    }

    private void currentStepDone() {
        if ((!inFluentStepSequence) && currentStepExists()) {
            TestStep finishedStep =  currentStepStack.pop();
            finishedStep.recordDuration();

            if (finishedStep == getCurrentGroup()) {
                finishGroup();
            }
        }
    }

    private boolean currentStepExists() {
        return !currentStepStack.isEmpty();
    }

    private void takeEndOfStepScreenshotFor(final TestResult result) {
        if (shouldTakeEndOfStepScreenshotFor(result)) {
            take(OPTIONAL_SCREENSHOT);
        }
    }

    private void take(final ScreenshotType screenshotType) {
        if (currentStepExists() && browserIsOpen()) {
            try {
                String stepDescription = getCurrentTestOutcome().getCurrentStep().getDescription();
                String testName = getCurrentTestOutcome().getTitle();
                Optional<ScreenshotAndHtmlSource> screenshotAndHtmlSource
                        = grabScreenshotFor(testName + ":" + stepDescription);
                if (screenshotAndHtmlSource.isPresent()) {
                    takeScreenshotIfRequired(screenshotType, screenshotAndHtmlSource.get());
                }
                removeDuplicatedInitalScreenshotsIfPresent();
            } catch (ScreenshotException e) {
                LOGGER.warn("Failed to take screenshot", e);
            }
        }
    }

    private void removeDuplicatedInitalScreenshotsIfPresent() {
        if (currentStepHasMoreThanOneScreenshot() && getPreviousStep().isPresent()) {
            ScreenshotAndHtmlSource lastScreenshotOfPreviousStep = lastScreenshotOf(getPreviousStep().get());
            ScreenshotAndHtmlSource firstScreenshotOfThisStep = getCurrentStep().getFirstScreenshot();
            if (haveIdenticalScreenshots(firstScreenshotOfThisStep, lastScreenshotOfPreviousStep)) {
                removeFirstScreenshotOfCurrentStep();
            }
        }
    }

    private void removeFirstScreenshotOfCurrentStep() {
        getCurrentStep().removeScreenshot(0);
    }

    private boolean currentStepHasMoreThanOneScreenshot() {
        return getCurrentStep().getScreenshotCount() > 1;
    }

    private ScreenshotAndHtmlSource lastScreenshotOf(TestStep testStep) {
        if (!testStep.getScreenshots().isEmpty()) {
            return testStep.getScreenshots().get(testStep.getScreenshots().size() - 1);
        }
        return null;
    }

    private void takeScreenshotIfRequired(ScreenshotType screenshotType, ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        if (shouldTakeScreenshot(screenshotType, screenshotAndHtmlSource) && screenshotWasTaken(screenshotAndHtmlSource)) {
            getCurrentStep().addScreenshot(screenshotAndHtmlSource);
        }
    }

    private boolean screenshotWasTaken(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        return screenshotAndHtmlSource.getScreenshotFile() != null;
    }


    private boolean shouldTakeScreenshot(ScreenshotType screenshotType,
                                         ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        return (screenshotType == MANDATORY_SCREENSHOT)
                || getCurrentStep().getScreenshots().isEmpty()
                || shouldTakeOptionalScreenshot(screenshotAndHtmlSource);
    }

    private boolean shouldTakeOptionalScreenshot(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        return (screenshotAndHtmlSource.wasTaken()
                && (!sameAsPreviousScreenshot(screenshotAndHtmlSource)));
    }

    private boolean sameAsPreviousScreenshot(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        try {
            Optional<Screenshot> screenshot = latestScreenshot();
            if (screenshot.isPresent()) {
                File screenshotTargetDirectory = new File(screenshotAndHtmlSource.getScreenshotFile().getParent());
                File screenshotFile = new File(screenshotTargetDirectory, screenshot.get().getFilename());
                return (checksumCRC32(screenshotFile) == checksumCRC32(screenshotAndHtmlSource.getScreenshotFile()));
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to compare screenshots: " + e.getMessage());
        }
        return false;
    }

    private boolean haveIdenticalScreenshots(ScreenshotAndHtmlSource screenshotAndHtmlSource,
                                             ScreenshotAndHtmlSource anotherScreenshotAndHtmlSource) {
        if (noScreenshotIn(screenshotAndHtmlSource) || noScreenshotIn(anotherScreenshotAndHtmlSource)) {
            return false;
        }
        try {
            File screenshotTargetDirectory = new File(screenshotAndHtmlSource.getScreenshotFile().getParent());
            File screenshot = new File(screenshotTargetDirectory,
                                       screenshotAndHtmlSource.getScreenshotFile().getName());
            File anotherScreenshot = new File(screenshotTargetDirectory,
                                              anotherScreenshotAndHtmlSource.getScreenshotFile().getName());
            return (checksumCRC32(screenshot) == checksumCRC32(anotherScreenshot));
        } catch (IOException e) {
            LOGGER.warn("Failed to compare screenshots: " + e.getMessage());
        }
        return false;
    }

    private boolean noScreenshotIn(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        return ((screenshotAndHtmlSource == null) || (screenshotAndHtmlSource.getScreenshotFile() == null));
    }

    private Optional<Screenshot> latestScreenshot() {
        List<Screenshot> screenshotsToDate = getCurrentTestOutcome().getScreenshots();
        if (!screenshotsToDate.isEmpty()) {
            return Optional.of(screenshotsToDate.get(screenshotsToDate.size() - 1));
        }
        return Optional.absent();
    }

    private boolean browserIsOpen() {
        if (driver == null) {
            return false;
        }
        if (driver instanceof  WebDriverFacade) {
            return (((WebDriverFacade) driver).isInstantiated());
        } else {
            return (driver.getCurrentUrl() != null);
        }
    }

    private void takeInitialScreenshot() {
        if ((currentStepExists()) && (screenshots().areAllowed(TakeScreenshots.BEFORE_AND_AFTER_EACH_STEP))) {
            take(OPTIONAL_SCREENSHOT);
        }
    }

    private Optional<ScreenshotAndHtmlSource> grabScreenshotFor(final String testName) {
        String snapshotName = underscore(testName);
        // TODO: Use unique identifier instead of test name
        Optional<File> screenshot = getPhotographer().takeScreenshot(snapshotName);
        if (screenshot.isPresent()) {
            File sourcecode = getPhotographer().getMatchingSourceCodeFor(screenshot.get());
            return Optional.of(new ScreenshotAndHtmlSource(screenshot.get(), sourcecode));
        }
        return Optional.absent();
    }

    public Photographer getPhotographer() {
        return new Photographer(driver, outputDirectory, new ScreenshotBlurCheck().blurLevel());

    }

    private boolean shouldTakeEndOfStepScreenshotFor(final TestResult result) {
        if (result == FAILURE) {
            return screenshots().areAllowed(TakeScreenshots.FOR_FAILURES);
        } else {
            return screenshots().areAllowed(TakeScreenshots.AFTER_EACH_STEP);
        }
    }

    public List<TestOutcome> getTestOutcomes() {
        List<TestOutcome> sortedOutcomes = Lists.newArrayList(testOutcomes);
        Collections.sort(sortedOutcomes, byStartTimeAndName());
        return ImmutableList.copyOf(sortedOutcomes);
    }

    private Comparator<? super TestOutcome> byStartTimeAndName() {
        return new Comparator<TestOutcome>() {
            public int compare(TestOutcome testOutcome1, TestOutcome testOutcome2) {
                String creationTimeAndName1 = testOutcome1.getStartTime().getMillis() + "_" + testOutcome1.getMethodName();
                String creationTimeAndName2 = testOutcome2.getStartTime().getMillis() + "_" + testOutcome2.getMethodName();
                return creationTimeAndName1.compareTo(creationTimeAndName2);
            }
        };
    }


    public void setDriver(final WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public boolean aStepHasFailed() {
        return (!getTestOutcomes().isEmpty()) && (getCurrentTestOutcome().getTestFailureCause() != null);
    }

    public Throwable getTestFailureCause() {
        return getCurrentTestOutcome().getTestFailureCause();
    }

    public void testFailed(TestOutcome testOutcome, final Throwable cause) {
        getCurrentTestOutcome().setTestFailureCause(cause);
    }

    public void testIgnored() {
        if (getCurrentTestOutcome().getResult() != PENDING) {
            getCurrentTestOutcome().setAnnotatedResult(IGNORED);
        }
    }

    public void notifyScreenChange() {
        if (screenshots().areAllowed(TakeScreenshots.FOR_EACH_ACTION)) {
            take(OPTIONAL_SCREENSHOT);
       }
    }

    int currentExample = 0;
    /**
     * The current scenario is a data-driven scenario using test data from the specified table.
     */
    public void useExamplesFrom(DataTable table) {
        getCurrentTestOutcome().useExamplesFrom(table);
        currentExample = 0;
    }

    public void exampleStarted(Map<String,String> data) {
        if (getCurrentTestOutcome().isDataDriven() && !getCurrentTestOutcome().dataIsPredefined()) {
            getCurrentTestOutcome().addRow(data);
        }
        currentExample++;
        StepEventBus.getEventBus().stepStarted(ExecutedStepDescription.withTitle(exampleTitle(currentExample, data)));
    }

    private String exampleTitle(int exampleNumber, Map<String, String> data) {
        return String.format("[%s] %s", exampleNumber, data);
    }

    public void exampleFinished() {
        getCurrentTestOutcome().moveToNextRow();
        stepFinished();
    }
}
