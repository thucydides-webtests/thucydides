package net.thucydides.junit.steps;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;
import static net.thucydides.core.util.NameConverter.underscore;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
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

/**
 * Observes the test run and stores test run details for later reporting.
 * Observations are recorded in an AcceptanceTestRun object. This includes
 * recording the names and results of each test, and taking and storing
 * screenshots at strategic points during the tests.
 * 
 * @author johnsmart
 * 
 */
public class ScenarioStepListener extends RunListener {

    private final List<AcceptanceTestRun> acceptanceTestRuns;
    private final Photographer photographer;
    private AcceptanceTestRun currentAcceptanceTestRun;
    private ConcreteTestStep currentTestStep;

    public ScenarioStepListener(final TakesScreenshot driver, final Configuration configuration) {
        acceptanceTestRuns = new ArrayList<AcceptanceTestRun>();

        TakesScreenshot screenshotCapableDriver = (TakesScreenshot) driver;
        photographer = new Photographer(screenshotCapableDriver, configuration.getOutputDirectory());

    }

    public List<AcceptanceTestRun> getTestRunResults() {
        return acceptanceTestRuns;
    }

    private void getCurrentTestStepFrom(final Description description) {
        if (currentTestStep == null) {
            currentTestStep = new ConcreteTestStep();
        }
    }

    private void recordCurrentTestStep(final Description description) {
        getCurrentTestStepFrom(description);
        addAnyTestedRequirementsIn(description);
        AnnotatedDescription testDescription = new AnnotatedDescription(description);
        String testName = testDescription.getName();
        currentTestStep.setDescription(testName);
        currentTestStep.recordDuration();
        currentAcceptanceTestRun.recordStep(currentTestStep);
        currentAcceptanceTestRun.recordDuration();
        currentTestStep = null;
    }

    private void addAnyTestedRequirementsIn(final Description description) {
        AnnotatedDescription testDescription = new AnnotatedDescription(description);
        List<String> requirements = testDescription.getAnnotatedRequirements();
        if (!requirements.isEmpty()) {
            for (String requirement : requirements) {
                currentTestStep.testsRequirement(requirement);
            }
        }
    }

    private File grabScreenshotFileFor(final String testName) throws IOException {
        String snapshotName = underscore(testName);
        File screenshot = null;
        try {
            screenshot = getPhotographer().takeScreenshot(snapshotName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenshot;
    }

    public Photographer getPhotographer() {
        return photographer;
    }

    @Override
    public void testRunStarted(final Description description) throws Exception {
        currentAcceptanceTestRun = new AcceptanceTestRun();
        currentAcceptanceTestRun.setMethodName(description.getMethodName());
        currentAcceptanceTestRun.setUserStory(withUserStoryFrom(description));
        acceptanceTestRuns.add(currentAcceptanceTestRun);
        updateTestRunTitleBasedOn(description);
        updateTestRunRequirementsBasedOn(description);
        getCurrentTestStepFrom(description);
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        System.out.println("Test started for " + description);
        super.testStarted(description);
        getCurrentTestStepFrom(description);
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

        getCurrentTestStepFrom(description);
        markCurrentTestAs(IGNORED);

        AnnotatedDescription testDescription = new AnnotatedDescription(description);
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

        getCurrentTestStepFrom(failure.getDescription());
        markCurrentTestAs(FAILURE);
        takeScreenshotFor(failure.getDescription());
        recordCurrentTestStep(failure.getDescription());
    }

    @Override
    public void testFinished(final Description description) throws Exception {

        getCurrentTestStepFrom(description);
        markCurrentTestAs(SUCCESS);
        takeScreenshotFor(description);

        recordCurrentTestStep(description);
    }

    private void takeScreenshotFor(final Description description) throws IOException {
        File screenshot = grabScreenshotFileFor(aTestCalled(description));
        currentTestStep.setScreenshot(screenshot);
    }

    protected String aTestCalled(final Description description) {
        return description.getMethodName();
    }

    private void updateTestRunTitleBasedOn(final Description description) {
        if (currentAcceptanceTestRun.getTitle() == null) {
            AnnotatedDescription testDescription = new AnnotatedDescription(description);
            currentAcceptanceTestRun.setTitle(testDescription.getTitle());
        }
    }

    private void updateTestRunRequirementsBasedOn(final Description description) {
        AnnotatedDescription testDescription = new AnnotatedDescription(description);
        List<String> requirements = testDescription.getAnnotatedRequirements();
        for(String requirement : requirements) {
            currentAcceptanceTestRun.testsRequirement(requirement);
        }
    }


}
