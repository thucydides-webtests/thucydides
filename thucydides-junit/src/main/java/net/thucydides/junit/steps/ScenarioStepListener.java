package net.thucydides.junit.steps;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;
import static net.thucydides.core.util.NameConverter.underscore;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.UserStory;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.util.NameConverter;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.junit.annotations.UserStoryCode;
import net.thucydides.junit.internals.TestStatus;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observes the test run and stores test run details for later reporting.
 * Observations are recorded in an AcceptanceTestRun object. This includes
 * recording the names and results of each test, and taking and storing
 * screenshots at strategic points during the tests.
 *
 * @author johnsmart
 */
public class ScenarioStepListener extends RunListener {

    private final List<AcceptanceTestRun> acceptanceTestRuns;
    private final Photographer photographer;
    private AcceptanceTestRun currentAcceptanceTestRun;
    private ConcreteTestStep currentTestStep;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioStepListener.class);

    public ScenarioStepListener(final TakesScreenshot driver, final Configuration configuration) {
        acceptanceTestRuns = new ArrayList<AcceptanceTestRun>();
        photographer = new Photographer(driver, configuration.getOutputDirectory());

    }

    public List<AcceptanceTestRun> getTestRunResults() {
        return acceptanceTestRuns;
    }

    private void recordCurrentTestStep(final Description description) {
        startNewTestStep();
        addAnyTestedRequirementsIn(description);

        String testName = AnnotatedDescription.from(description).getName();
        currentTestStep.setDescription(testName);
        currentTestStep.recordDuration();

        getCurrentAcceptanceTestRun().recordStep(currentTestStep);
        getCurrentAcceptanceTestRun().recordDuration();
        finishTestStep();
    }

    private void startNewTestStep() {
        if (currentTestStep == null) {
            currentTestStep = new ConcreteTestStep();
        }
    }

    private void finishTestStep() {
        currentTestStep = null;
    }

    private void addAnyTestedRequirementsIn(final Description description) {
        AnnotatedDescription testDescription = AnnotatedDescription.from(description);
        List<String> requirements = testDescription.getAnnotatedRequirements();
        if (!requirements.isEmpty()) {
            for (String requirement : requirements) {
                currentTestStep.testsRequirement(requirement);
            }
        }
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

    @Override
    public void testRunStarted(final Description description) throws Exception {
        startNewCurrentAcceptanceTestRun();
        getCurrentAcceptanceTestRun().setMethodName(description.getMethodName());
        getCurrentAcceptanceTestRun().setUserStory(withUserStoryFrom(description));
        acceptanceTestRuns.add(getCurrentAcceptanceTestRun());
        updateTestRunTitleBasedOn(description);
        updateTestRunRequirementsBasedOn(description);
        startNewTestStep();
    }


    @Override
    public void testStarted(final Description description) throws Exception {
        AnnotatedDescription testDescription = AnnotatedDescription.from(description);

        if (testDescription.isAGroup()) {
            testGroupStarted(testDescription);
        } else {
            super.testStarted(description);
            startNewTestStep();
        }
    }

    public void testGroupStarted(final AnnotatedDescription testDescription) {
        getCurrentAcceptanceTestRun().startGroup(testDescription.getGroupName());
    }

    private UserStory withUserStoryFrom(final Description description) {
        String name = NameConverter.humanize(description.getTestClass().getSimpleName());
        String code = userStoryCodeFromAnnotationIfPresentIn(description.getTestClass());
        String source = description.getTestClass().getCanonicalName();
        return new UserStory(name, code, source);
    }

    private String userStoryCodeFromAnnotationIfPresentIn(final Class<?> testClass) {
        UserStoryCode userStoryAnnotation = testClass.getAnnotation(UserStoryCode.class);
        if (userStoryAnnotation != null) {
            return userStoryAnnotation.value();
        } else {
            return "";
        }
    }

    @Override
    public void testIgnored(final Description description) throws Exception {

        startNewTestStep();
        markCurrentTestAs(IGNORED);

        AnnotatedDescription testDescription = AnnotatedDescription.from(description);
        Method testMethod = testDescription.getTestMethod();
        if (TestStatus.of(testMethod).isPending()) {
            markCurrentTestAs(PENDING);
        } else if (TestStatus.of(testMethod).isIgnored()) {
            markCurrentTestAs(IGNORED);
        } else {
            markCurrentTestAs(SKIPPED);
        }
        recordCurrentTestStep(description);

    }

    private void markCurrentTestAs(final TestResult result) {
        currentTestStep.setResult(result);
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {

        startNewTestStep();
        markCurrentTestAs(FAILURE);
        recordFailureDetailsInFailingTestStep(failure);
        takeScreenshotFor(failure.getDescription());
        recordCurrentTestStep(failure.getDescription());
    }

    private void recordFailureDetailsInFailingTestStep(final Failure failure) {
        if (!currentTestStep.isAGroup()) {
            currentTestStep.failedWith(failure.getMessage(), failure.getException());
        }
    }

    @Override
    public void testFinished(final Description description) throws Exception {

        AnnotatedDescription testDescription = AnnotatedDescription.from(description);

        if (testDescription.isAGroup()) {
            getCurrentAcceptanceTestRun().endGroup();
        } else {
            startNewTestStep();
            markCurrentTestAs(SUCCESS);
            takeScreenshotFor(description);
            recordCurrentTestStep(description);
            pauseIfRequired();
        }
    }

    private void pauseIfRequired() {
        int delay = Configuration.getStepDelay();
        if (delay > 0) {
            pauseTestRun(delay);
        }
    }

    public void pauseTestRun(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            LOGGER.error("Wait interrupted", e);
        }
    }


    private void takeScreenshotFor(final Description description) {
        File screenshot = grabScreenshotFileFor(aTestCalled(description));
        currentTestStep.setScreenshot(screenshot);
    }

    protected String aTestCalled(final Description description) {
        return description.getMethodName();
    }

    private void updateTestRunTitleBasedOn(final Description description) {
        if (getCurrentAcceptanceTestRun().getTitle() == null) {
            AnnotatedDescription testDescription = AnnotatedDescription.from(description);
            getCurrentAcceptanceTestRun().setTitle(testDescription.getTitle());
        }
    }

    private void updateTestRunRequirementsBasedOn(final Description description) {
        AnnotatedDescription testDescription = AnnotatedDescription.from(description);
        List<String> requirements = testDescription.getAnnotatedRequirements();
        for (String requirement : requirements) {
            getCurrentAcceptanceTestRun().testsRequirement(requirement);
        }
    }


}
