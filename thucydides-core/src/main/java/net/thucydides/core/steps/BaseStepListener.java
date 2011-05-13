package net.thucydides.core.steps;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestStepGroup;
import net.thucydides.core.model.UserStory;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.util.NameConverter;
import net.thucydides.core.webdriver.Configuration;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;
import static net.thucydides.core.util.NameConverter.underscore;

/**
 * Observes the test run and stores test run details for later reporting.
 * Observations are recorded in an AcceptanceTestRun object. This includes
 * recording the names and results of each test, and taking and storing
 * screenshots at strategic points during the tests.
 *
 * @author johnsmart
 */
public class BaseStepListener implements StepListener {

    private final List<AcceptanceTestRun> acceptanceTestRuns;
    private final Photographer photographer;
    private AcceptanceTestRun currentAcceptanceTestRun;
    private ConcreteTestStep currentTestStep;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStepListener.class);

    public BaseStepListener(final TakesScreenshot driver, final File outputDirectory) {
        acceptanceTestRuns = new ArrayList<AcceptanceTestRun>();
        photographer = new Photographer(driver, outputDirectory);
    }

    public List<AcceptanceTestRun> getTestRunResults() {
        return acceptanceTestRuns;
    }

    private void recordCurrentTestStep(final ExecutedStepDescription description) {

        getCurrentStep().setDescription(description.getName());
        getCurrentStep().recordDuration();
        getCurrentAcceptanceTestRun().recordStep(currentTestStep);
        getCurrentAcceptanceTestRun().recordDuration();
        finishTestStep();
    }

    private void startNewTestStep() {
        currentTestStep = new ConcreteTestStep();
    }

    private void finishTestStep() {
        currentTestStep = null;
    }

    private File grabScreenshotFileFor(final String testName) {
        String snapshotName = underscore(testName);
        File screenshot = null;
        try {
            screenshot = getPhotographer().takeScreenshot(snapshotName);
        } catch (Exception e) {
            LOGGER.error("Failed to save screenshot file", e);
        }
        return screenshot;
    }

    public Photographer getPhotographer() {
        return photographer;
    }

    protected AcceptanceTestRun getCurrentAcceptanceTestRun() {
        if (currentAcceptanceTestRun == null) {
            currentAcceptanceTestRun = new AcceptanceTestRun();
        }
        return currentAcceptanceTestRun;
    }

    protected void startNewCurrentAcceptanceTestRun() {
        currentAcceptanceTestRun = null;
        getCurrentAcceptanceTestRun();
    }

    public void testRunStarted(final String description) {
        testRunStarted(ExecutedStepDescription.withTitle(description));
    }

    public void testRunStarted(final ExecutedStepDescription description) {
        startNewCurrentAcceptanceTestRun();
        if (description.getTestMethod() != null) {
            getCurrentAcceptanceTestRun().setMethodName(description.getTestMethod().getName());
            getCurrentAcceptanceTestRun().setUserStory(withUserStoryFromTestCaseIn(description));
        } else {
            getCurrentAcceptanceTestRun().setUserStory(withUserStoryFrom(description));
        }
        setTitleIfNotAlreadySet();

        acceptanceTestRuns.add(getCurrentAcceptanceTestRun());
    }

    private void setTitleIfNotAlreadySet() {
        String testRunTitle = getCurrentAcceptanceTestRun().getUserStory().getName();
        getCurrentAcceptanceTestRun().setTitle(testRunTitle);
    }

    public void testGroupStarted(final ExecutedStepDescription description) {
        getCurrentAcceptanceTestRun().startGroup(description.getName());
        takeScreenshotForCurrentGroup();
    }

    private void takeScreenshotForCurrentGroup() {
        TestStepGroup currentGroup = getCurrentAcceptanceTestRun().getCurrentGroup();
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

    private UserStory withUserStoryFromTestCaseIn(final ExecutedStepDescription description) {
        String name = NameConverter.humanize(description.getStepClass().getSimpleName());
        String source = description.getStepClass().getCanonicalName();
        return new UserStory(name, "", source);
    }

    private UserStory withUserStoryFrom(final ExecutedStepDescription description) {
        String name = NameConverter.humanize(description.getName());
        return new UserStory(name, "", "");
    }

    private void markCurrentTestAs(final TestResult result) {
        getCurrentStep().setResult(result);
    }

    private TestStep getCurrentStep() {
        if (currentTestStep != null) {
            return currentTestStep;
        } else {
            return getCurrentAcceptanceTestRun().getCurrentGroup();
        }
    }

    private void recordFailureDetailsInFailingTestStep(final StepFailure failure) {
        getCurrentStep().failedWith(failure.getMessage(), failure.getException());
    }

    private void pauseIfRequired() {
        int delay = Configuration.getStepDelay();
        if (delay > 0) {
            pauseTestRun(delay);
        }
    }

    public void pauseTestRun(final long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            LOGGER.error("Wait interrupted", e);
        }
    }


    private void takeScreenshotFor(final ExecutedStepDescription description) {
        File screenshot = grabScreenshotFileFor(aTestCalled(description));
        getCurrentStep().setScreenshot(screenshot);
        File sourcecode = getPhotographer().getMatchingSourceCodeFor(screenshot);
        getCurrentStep().setHtmlSource(sourcecode);
    }

    protected String aTestCalled(final ExecutedStepDescription description) {
        return description.getName();
    }

    public void stepStarted(ExecutedStepDescription description) {
        if (stepIsAGroup(description)) {
            testGroupStarted(description);
        } else {
            startNewTestStep();
        }
    }

    public void stepFinished(final ExecutedStepDescription description) {
        if (stepIsAGroup(description)) {
            getCurrentAcceptanceTestRun().endGroup();
        } else {
            markCurrentTestAs(SUCCESS);
            takeScreenshotFor(description);
            recordCurrentTestStep(description);
            pauseIfRequired();
        }
    }

    public void stepGroupStarted(final String description) {
        stepGroupStarted(ExecutedStepDescription.withTitle(description));
    }

    public void stepGroupStarted(ExecutedStepDescription description) {
        ExecutedStepDescription copiedDescription = description.clone();
        copiedDescription.setAGroup(true);
        testGroupStarted(copiedDescription);
    }

    public void stepGroupFinished() {
        getCurrentAcceptanceTestRun().endGroup();
    }

    public void stepGroupFinished(final TestResult result) {
        getCurrentAcceptanceTestRun().setDefaultGroupResult(result);
        getCurrentAcceptanceTestRun().endGroup();
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
        getCurrentAcceptanceTestRun().updateMostResultTestStepResult(result);
    }

    public void stepFailed(final StepFailure failure) {
        markCurrentTestAs(FAILURE);
        recordFailureDetailsInFailingTestStep(failure);
        takeScreenshotFor(failure.getDescription());
        if (currentTestStep != null) {
            recordCurrentTestStep(failure.getDescription());
        }
    }

    private boolean stepIsAGroup(final ExecutedStepDescription description) {
        if (description.isAGroup()) {
            return true;
        } else {
            return stepMethodIsAGroup(description.getTestMethod());
        }
    }
    public void stepIgnored(final ExecutedStepDescription description) {
        markCurrentTestAs(IGNORED);

        Method testMethod = description.getTestMethod();
        if (stepMethodIsPending(testMethod)) {
            markCurrentTestAs(PENDING);
        } else if (stepMethodIsIgnored(testMethod)) {
            markCurrentTestAs(IGNORED);
        } else {
            markCurrentTestAs(SKIPPED);
        }
        if (currentTestStep != null) {
            recordCurrentTestStep(description);
        }
    }

    private boolean stepMethodIsPending(final Method testMethod) {
        if (testMethod  != null) {
            return TestStatus.of(testMethod).isPending();
        } else {
            return false;
        }
    }

    private boolean stepMethodIsIgnored(final Method testMethod) {
        if (testMethod  != null) {
            return TestStatus.of(testMethod).isIgnored();
        } else {
            return false;
        }
    }

    private boolean stepMethodIsAGroup(final Method testMethod) {
        if (testMethod  != null) {
            return TestStatus.of(testMethod).isAStepGroup();
        } else {
            return false;
        }
    }

    public void testRunFinished(final TestStepResult result) {
    }

}
