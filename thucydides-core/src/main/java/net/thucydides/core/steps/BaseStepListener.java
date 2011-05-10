package net.thucydides.core.steps;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.UserStory;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.util.NameConverter;
import net.thucydides.core.webdriver.Configuration;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static net.thucydides.core.model.TestResult.*;
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
        System.out.println("Recording current test step");

        startNewTestStep();

        String testName = description.getName();
        currentTestStep.setDescription(testName);
        currentTestStep.recordDuration();

        getCurrentAcceptanceTestRun().recordStep(currentTestStep);
        getCurrentAcceptanceTestRun().recordDuration();
        System.out.println("CurrentAcceptanceTestRun step count: " + getCurrentAcceptanceTestRun().getStepCount());
        System.out.println("acceptanceTestRuns: " + acceptanceTestRuns);
        finishTestStep();
    }

    private void startNewTestStep() {
        if (currentTestStep == null) {
            currentTestStep = new ConcreteTestStep();
        }
    }

    private void finishTestStep() {
        System.out.println("Acceptance test runs: " + acceptanceTestRuns);
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
        System.out.println("Starting test run " + description.getTitle());
        startNewCurrentAcceptanceTestRun();
        if (description.getTestMethod() != null) {
            getCurrentAcceptanceTestRun().setMethodName(description.getTestMethod().getName());
            if (getCurrentAcceptanceTestRun().getUserStory() == null)
                getCurrentAcceptanceTestRun().setUserStory(withUserStoryFromTestCaseIn(description));
        } else {
            if (getCurrentAcceptanceTestRun().getUserStory() == null)
                getCurrentAcceptanceTestRun().setUserStory(withUserStoryFrom(description));
        }
        setTitleIfNotAlreadySet();

        acceptanceTestRuns.add(getCurrentAcceptanceTestRun());
        startNewTestStep();
    }

    private void setTitleIfNotAlreadySet() {
        if (getCurrentAcceptanceTestRun() != null) {
            String testRunTitle = getCurrentAcceptanceTestRun().getUserStory().getName();
            getCurrentAcceptanceTestRun().setTitle(testRunTitle);
        }
    }

    private void setTestRunTitleFrom(ExecutedStepDescription description) {
    }

    public void testGroupStarted(final ExecutedStepDescription description) {
        System.out.println("Test Group Started for: " + description.getName());
        getCurrentAcceptanceTestRun().startGroup(description.getName());
    }

    private UserStory withUserStoryFromTestCaseIn(final ExecutedStepDescription description) {
        System.out.println("Creating user story from unit test class" + description.getStepClass().getSimpleName());
        String name = NameConverter.humanize(description.getStepClass().getSimpleName());
        String source = description.getStepClass().getCanonicalName();
        System.out.println("using canonical name " + description.getStepClass().getCanonicalName());
        return new UserStory(name, "", source);
    }

    private UserStory withUserStoryFrom(final ExecutedStepDescription description) {
        System.out.println("Creating user story from easyb story" + description.getName());
        String name = NameConverter.humanize(description.getName());
        return new UserStory(name, "", "");
    }

    private void markCurrentTestAs(final TestResult result) {
        if (currentTestStep != null) {
            currentTestStep.setResult(result);
            getCurrentAcceptanceTestRun().setDefaultGroupResult(result);
        }
    }

    private void recordFailureDetailsInFailingTestStep(final StepFailure failure) {
        if (!currentTestStep.isAGroup()) {
            currentTestStep.failedWith(failure.getMessage(), failure.getException());
        }
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
        currentTestStep.setScreenshot(screenshot);
        File sourcecode = getPhotographer().getMatchingSourceCodeFor(screenshot);
        currentTestStep.setHtmlSource(sourcecode);
    }

    protected String aTestCalled(final ExecutedStepDescription description) {
        return description.getName();
    }

    public void stepStarted(ExecutedStepDescription description) {
        if (description.isAGroup()) {
            testGroupStarted(description);
        } else {
            startNewTestStep();
        }
    }

    public void stepFinished(ExecutedStepDescription description) {

        System.out.println("STEP FINISHED (" + this + ")");
        if (description.isAGroup()) {
            System.out.println(" - End group");
            getCurrentAcceptanceTestRun().endGroup();
        } else {
            System.out.println(" - End step");
            if (testStepRunning()) {
                markCurrentTestAs(SUCCESS);
                takeScreenshotFor(description);
                recordCurrentTestStep(description);
            }
            pauseIfRequired();
        }
        System.out.println("TEST REPORT AFTER STEP FINISHED: " + getCurrentAcceptanceTestRun().toXML());
    }

    public void stepGroupStarted(String description) {
        stepGroupStarted(ExecutedStepDescription.withTitle(description));
    }

    private boolean testStepRunning() {
        return (currentTestStep != null);
    }

    public void stepGroupStarted(ExecutedStepDescription description) {
        ExecutedStepDescription copiedDescription = description.clone();
        copiedDescription.setAGroup(true);
        testGroupStarted(copiedDescription);
        System.out.println("TEST REPORT AFTER GROUP STARTED: " + getCurrentAcceptanceTestRun().toXML());
    }

    public void stepGroupFinished() {
        getCurrentAcceptanceTestRun().endGroup();
        System.out.println("TEST REPORT AFTER END GROUP: " + getCurrentAcceptanceTestRun().toXML());
    }

    public void stepSucceeded() {
        System.out.println("STEP SUCCEEDED");
        markCurrentTestAs(SUCCESS);
    }

    public void stepFailed(StepFailure failure) {
        System.out.println("STEP FAILED");
        markCurrentTestAs(FAILURE);
        recordFailureDetailsInFailingTestStep(failure);
        takeScreenshotFor(failure.getDescription());
        recordCurrentTestStep(failure.getDescription());
    }

    public void stepIgnored(ExecutedStepDescription description) {
        System.out.println("STEP IGNORED");
        markCurrentTestAs(IGNORED);

        Method testMethod = description.getTestMethod();
        if (TestStatus.of(testMethod).isPending()) {
            markCurrentTestAs(PENDING);
        } else if (TestStatus.of(testMethod).isIgnored()) {
            markCurrentTestAs(IGNORED);
        } else {
            markCurrentTestAs(SKIPPED);
        }
        recordCurrentTestStep(description);
    }

    public void testRunFinished(TestStepResult result) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
