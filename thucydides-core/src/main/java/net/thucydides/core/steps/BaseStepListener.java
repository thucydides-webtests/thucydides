package net.thucydides.core.steps;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestStepGroup;
import net.thucydides.core.pages.InternalClock;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.screenshots.ScreenshotException;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebdriverProxyFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;
import static net.thucydides.core.util.NameConverter.underscore;
 
/**
 * Observes the test run and stores test run details for later reporting.
 * Observations are recorded in an TestOutcome object. This includes
 * recording the names and results of each test, and taking and storing
 * screenshots at strategic points during the tests.
 *
 * @author johnsmart
 */
public class BaseStepListener implements StepListener {
 
    private final Collection<TestOutcome> testOutcomes;
    private TestOutcome currentTestOutcome;
    private Story testedStory;
    private Class<?> testClass;
    private ConcreteTestStep currentTestStep;
 
    private WebDriver driver;
    private File outputDirectory;
 
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStepListener.class);
 
    private boolean aStepHasFailed;
    private Throwable stepError;
    private InternalClock clock;

    private WebdriverProxyFactory proxyFactory;

    private BaseStepListener(final File outputDirectory) {
        this.proxyFactory = WebdriverProxyFactory.getFactory();
        this.testOutcomes = new ArrayList<TestOutcome>();
        this.clock = new InternalClock();
        this.outputDirectory = outputDirectory;
        aStepHasFailed = false;
        stepError = null;
    }

    protected WebdriverProxyFactory getProxyFactory() {
        return proxyFactory;
    }
 
    protected InternalClock getClock() {
        return clock;
    }

    public BaseStepListener(final Class<? extends WebDriver> driverClass, final File outputDirectory) {
        this(outputDirectory);
        this.driver = getProxyFactory().proxyFor(driverClass);
    }
 
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

    public void setDriver(final WebDriver driver) {
        this.driver = driver;
    }
 
    public WebDriver getDriver() {
        return driver;
    }
 
    public boolean aStepHasFailed() {
        return aStepHasFailed;
    }
 
    public void noStepsHaveFailed() {
        aStepHasFailed = false;
        stepError = null;
    }

    public boolean isDataDriven() {
        return DataDrivenStep.inProgress();
    }


    public Throwable getStepError() {
        return stepError;
    }
 
    public List<TestOutcome> getTestOutcomes() {
        return ImmutableList.copyOf(testOutcomes);
    }
 
    private void recordCurrentTestStep(final ExecutedStepDescription description) {
        if (currentTestStep != null) {
 
            addAnyTestedRequirementsIn(description);
 
            String testName = AnnotatedStepDescription.from(description).getName();
            getCurrentStep().setDescription(testName);
            getCurrentStep().recordDuration();
            if (getCurrentTestOutcome() != null) {
                getCurrentTestOutcome().recordStep(currentTestStep);
                getCurrentTestOutcome().recordDuration();
            }
 
            finishTestStep();
        }
    }
 
 
    private void startNewTestStep(final ExecutedStepDescription description) {
        currentTestStep = new ConcreteTestStep();
        currentTestStep.setDescription(description.getName());
    }
 
    private void finishTestStep() {
        currentTestStep = null;
    }
 
    private File grabScreenshotFileFor(final String testName) {
        String snapshotName = underscore(testName);
        return getPhotographer().takeScreenshot(snapshotName);
    }
 
    public Photographer getPhotographer() {
        return new Photographer(driver, outputDirectory);
 
    }
 
    protected TestOutcome getCurrentTestOutcome() {
        return currentTestOutcome;
    }
 
    protected void startNewTestOutcomeFor(final String testName, final Story story) {
        LOGGER.debug("startNewTestOutcomeFor {}", testName);

        this.testedStory = story;
        currentTestOutcome = TestOutcome.forTestInStory(testName, testedStory, testClass);
        testOutcomes.add(currentTestOutcome);
        aStepHasFailed = false;
    }

    public void testRunStartedFor(final Class<?> testClass) {
        this.testClass = testClass;
        Story story = findStoryFrom(testClass);
        testRunStartedFor(story);
    }

    private Story findStoryFrom(Class<?> testClass) {
        Story story = null;
        if (storyIsDefinedIn(testClass)) {
            story = storyFrom(testClass);
        } else {
            story = Story.from(testClass);
        }
        return story;
    }

    public void testRunStartedFor(final Story story) {
        this.testedStory = story;
    }

    private Story storyFrom(final Class<?> testClass) {
        Class<?> testedStoryClass = Story.testedInTestCase(testClass);
        if (testedStoryClass != null) {
            return Story.from(testedStoryClass);
        }
        return null;
    }

    private boolean storyIsDefinedIn(final Class<?> testClass) {
        return (storyFrom(testClass) != null);
    }

    public void testStarted(final String testName) {
        LOGGER.debug("Starting test: {}", testName);
        startNewTestOutcomeFor(testName, testedStory);
        getCurrentTestOutcome().setMethodName(testName);
    }

    private void addAnyTestedRequirementsIn(final ExecutedStepDescription description) {
        AnnotatedStepDescription testStepDescription = AnnotatedStepDescription.from(description);
        List<String> requirements = testStepDescription.getAnnotatedRequirements();
        for (String requirement : requirements) {
            currentTestStep.testsRequirement(requirement);
        }
    }

    public void testGroupStarted(final ExecutedStepDescription description) {
        if (getCurrentTestOutcome() == null) {
            startNewTestOutcomeFor(description.getName(), testedStory);
            getCurrentTestOutcome().startGroup(description.getName());
        } else {
            getCurrentTestOutcome().startGroup(description.getName());
        }
        takeScreenshotForCurrentGroup();
    }

    private void takeScreenshotForCurrentGroup() {
        TestStepGroup currentGroup = getCurrentTestOutcome().getCurrentGroup();
        takeScreenshotForGroup(currentGroup);
    }

    private void takeScreenshotForGroup(final TestStepGroup group) {
        File screenshot = grabScreenshotFileFor(group.getDescription());
        group.setScreenshot(screenshot);
        if (screenshot != null) {
            File sourcecode = getPhotographer().getMatchingSourceCodeFor(screenshot);
            group.setHtmlSource(sourcecode);
        }
    }

    private void markCurrentTestAs(final TestResult result) {
        if (failureOccursBeforeAnyStepsHaveBeenExecuted(result)) {
            stepStarted(ExecutedStepDescription.withTitle("undefined"));
        }

        if (getCurrentStep() != null) {
            getCurrentStep().setResult(result);
        } else if ((result == FAILURE) && (getTestOutcomes().isEmpty())) {
            startNewTestStep(ExecutedStepDescription.withTitle("undefined"));
        }
    }

    private boolean failureOccursBeforeAnyStepsHaveBeenExecuted(final TestResult result) {
        return ((result == FAILURE) && (getCurrentStep() == null) && (getCurrentTestOutcome().getStepCount() == 0));
    }
    private TestStep getCurrentStep() {
        if (currentTestStep != null) {
            return currentTestStep;
        } else {
            return getCurrentTestOutcome().getCurrentGroup();
        }
    }
 
    private void recordFailureDetailsInFailingTestStep(final StepFailure failure) {
        if (currentTestStep != null) {
          getCurrentStep().failedWith(failure.getMessage(), failure.getException());
        }
    }
 
    private void pauseIfRequired() {
        int delay = Configuration.getStepDelay();
        if (delay > 0) {
            getClock().pauseFor(delay);
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

    private boolean shouldTakeScreenshotFor(final TestResult result) {
        String onlySaveFailures = System.getProperty(ThucydidesSystemProperty.ONLY_SAVE_FAILING_SCREENSHOTS.getPropertyName(), "false");
        Boolean onlySaveFailureScreenshots = Boolean.valueOf(onlySaveFailures);
        if (onlySaveFailureScreenshots && result != FAILURE) {
            return false;
        }
        return true;
    }

    protected String aTestCalled(final ExecutedStepDescription description) {
        return description.getName();
    }
 
    public void stepStarted(final ExecutedStepDescription description) {
        if (stepIsAGroup(description)) {
            testGroupStarted(description);
        } else {
            startNewTestStep(description);
        }
    }
 
    public void stepFinished(final ExecutedStepDescription description) {
        if (stepIsAGroup(description)) {
            takeScreenshotForCurrentGroup();
            getCurrentTestOutcome().endGroup();
        } else {
            markCurrentTestAs(SUCCESS);
            takeScreenshotFor(description, SUCCESS);
            recordCurrentTestStep(description);
        }
        pauseIfRequired();
    }



    public void stepGroupStarted(final String description) {
        stepGroupStarted(ExecutedStepDescription.withTitle(description));
    }
 
    public void stepGroupStarted(final ExecutedStepDescription description) {
        ExecutedStepDescription copiedDescription = description.clone();
        copiedDescription.setAGroup(true);
        testGroupStarted(copiedDescription);
    }
 
    public void stepGroupFinished() {
        if (currentTestOutcome != null) {
            getCurrentTestOutcome().endGroup();
        }
    }
 
    public void stepGroupFinished(final TestResult result) {
        if (getCurrentTestOutcome() != null) {
            getCurrentTestOutcome().setDefaultGroupResult(result);
            getCurrentTestOutcome().endGroup();
        }
    }
 
    public void stepSucceeded() {
        markCurrentTestAs(SUCCESS);
    }
 
    /**
     * Update the status of the current step (e.g to IGNORED or SKIPPED) without changing anything else.
     */
    public void updateCurrentStepStatus(final TestResult result) {
        if (currentTestStep == null) {
            updateMostRecentStepStatus(result);
        } else {
            markCurrentTestAs(result);
        }
    }
 
    private void updateMostRecentStepStatus(final TestResult result) {
        getCurrentTestOutcome().updateMostResultTestStepResult(result);
    }
 
    public void stepFailed(final StepFailure failure) {
        stepFailedWith(failure);

        markCurrentTestAs(FAILURE);
        recordFailureDetailsInFailingTestStep(failure);
        takeScreenshotFor(failure.getDescription(), FAILURE);
        if (currentTestStep != null) {
            recordCurrentTestStep(failure.getDescription());
        }
    }

    private void stepFailedWith(final StepFailure failure) {
        aStepHasFailed = true;
        stepError = failure.getException();
    }
 
    private boolean stepIsAGroup(final ExecutedStepDescription description) {
        return (description.isAGroup() || AnnotatedStepDescription.from(description).isAGroup());
    }

    public void stepIgnored(final ExecutedStepDescription description) {

        ensureThatTestHasStartedFor(description);

        if (AnnotatedStepDescription.from(description).isPending()) {
            markCurrentTestAs(PENDING);
        } else if (AnnotatedStepDescription.from(description).isIgnored()) {
            ignoreStepMethodWith(description);
        } else {
            markCurrentTestAs(SKIPPED);
        }
        if (currentTestStep != null) {
            recordCurrentTestStep(description);
        }
    }

    private void ensureThatTestHasStartedFor(final ExecutedStepDescription description) {
        if (testRunNotStartedYet()) {
            testRunStartedFor(description.getStepClass());
        }
        if (testNotStartedYet()) {
            testStarted(description.getName());
        }
    }

    private boolean testRunNotStartedYet() {
        return (testedStory == null);
    }

    private boolean testNotStartedYet() {
        return (currentTestStep == null);
    }

    private void ignoreStepMethodWith(final ExecutedStepDescription description) {
        if (currentTestStep == null) {
            startNewTestStep(description);
        }
        markCurrentTestAs(IGNORED);
    }

 
    public void testFinished(final TestStepResult result) {
        LOGGER.debug("testFinished: ", result);
        currentTestOutcome = null;
    }
 
}