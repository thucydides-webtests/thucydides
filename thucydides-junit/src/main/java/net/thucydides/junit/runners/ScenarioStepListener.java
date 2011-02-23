package net.thucydides.junit.runners;

import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.junit.annotations.StepDescription;
import net.thucydides.junit.annotations.TestsRequirement;
import net.thucydides.junit.annotations.TestsRequirements;
import net.thucydides.junit.annotations.Title;
import net.thucydides.junit.internals.TestStatus;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.modeshape.common.text.Inflector;
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
    private final Inflector inflector;
    private AcceptanceTestRun currentAcceptanceTestRun;
    private TestStep currentTestStep;

    public ScenarioStepListener(final TakesScreenshot driver, final Configuration configuration) {
        acceptanceTestRuns = new ArrayList<AcceptanceTestRun>();
        inflector = Inflector.getInstance();

        TakesScreenshot screenshotCapableDriver = (TakesScreenshot) driver;
        photographer = new Photographer(screenshotCapableDriver, configuration.getOutputDirectory());

    }

    public List<AcceptanceTestRun> getTestRunResults() {
        return acceptanceTestRuns;
    }

    private void getCurrentTestStepFrom(final Description description) {
        if (currentTestStep == null) {
            currentTestStep = new TestStep();
        }
    }

    private void recordCurrentTestStep(final Description description) {
        getCurrentTestStepFrom(description);
        addAnyTestedRequirementsIn(description);
        String testName = testNameFrom(description);
        currentTestStep.setDescription(testName);
        currentAcceptanceTestRun.recordStep(currentTestStep);
        currentTestStep = null;
    }

    private void addAnyTestedRequirementsIn(final Description description) {
        List<String> requirements = annotatedRequirementsOf(description);
        if (!requirements.isEmpty()) {
            for (String requirement : requirements) {
                currentTestStep.testsRequirement(requirement);
            }
        }
    }

    private File grabScreenshotFileFor(final String testName) throws IOException {
        String snapshotName = Inflector.getInstance().underscore(testName);
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
        acceptanceTestRuns.add(currentAcceptanceTestRun);
        updateTestRunTitleBasedOn(description);
        updateTestRunRequirementsBasedOn(description);
        getCurrentTestStepFrom(description);
    }

    @Override
    public void testIgnored(final Description description) throws Exception {

        getCurrentTestStepFrom(description);
        markCurrentTestAs(IGNORED);

        Method testMethod = getTestMethodFrom(description);
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
            currentAcceptanceTestRun.setTitle(fromTitleIn(description));
        }
    }

    private void updateTestRunRequirementsBasedOn(final Description description) {
        List<String> requirements = annotatedRequirementsOf(description);
        for(String requirement : requirements) {
            currentAcceptanceTestRun.testsRequirement(requirement);
        }
    }

    /**
     * Turns a method into a human-readable title.
     */
    protected String fromTitleIn(final Description description) {

        String annotationTitle = getAnnotatedTitleFrom(description);
        if (annotationTitle != null) {
            return inflector.humanize(annotationTitle);
        } else {
            String testMethodName = description.getMethodName();
            return inflector.humanize(testMethodName);
        }
    }

    // REFACTOR INTO EXTERNAL CLASS
    private String testNameFrom(final Description description) {
        String annotatedDescription = annotatedDescriptionOf(description);
        if (annotatedDescription != null) {
            return annotatedDescription;
        }
        return humanizedTestNameFrom(description);
    }

    protected String annotatedDescriptionOf(final Description description) {
        String annotatedDescription = null;
        try {
            Method testMethod = getTestMethodFrom(description);
            StepDescription stepDescription = (StepDescription) testMethod
                    .getAnnotation(StepDescription.class);
            if (stepDescription != null) {
                annotatedDescription = stepDescription.value();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return annotatedDescription;
    }

    private Method getTestMethodFrom(final Description description) throws NoSuchMethodException {
        return description.getTestClass().getDeclaredMethod(description.getMethodName(),
                (Class[]) null);
    }

    private String getAnnotatedTitleFrom(final Description description) {
        try {
            Method testMethod = getTestMethodFrom(description);
            Title title = (Title) testMethod.getAnnotation(Title.class);
            if (title != null) {
                return title.value();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected List<String> annotatedRequirementsOf(final Description description) {
        List<String> requirements = new ArrayList<String>();
        try {
            Method testMethod = getTestMethodFrom(description);
            addRequirementFrom(requirements, testMethod);
            addMultipleRequirementsFrom(requirements, testMethod);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return requirements;
    }

    private void addMultipleRequirementsFrom(final List<String> requirements,final Method testMethod) {
        TestsRequirements testRequirements = (TestsRequirements) testMethod.getAnnotation(TestsRequirements.class);
        if (testRequirements != null) {
            for(String requirement : testRequirements.value()) {
                requirements.add(requirement);
            }
        }
    }

    private void addRequirementFrom(final List<String> requirements, final Method testMethod) {
        TestsRequirement testsRequirement = (TestsRequirement) testMethod
                .getAnnotation(TestsRequirement.class);
        if (testsRequirement != null) {
            requirements.add(testsRequirement.value());
        }
    }

    /**
     * Turns a classname into a human-readable title.
     */
    private String humanizedTestNameFrom(final Description description) {

        String testName = description.getMethodName();
        String humanizedName = inflector.humanize(inflector.underscore(testName));
        if (!humanizedName.endsWith(".")) {
            humanizedName = humanizedName + ".";
        }
        return humanizedName;
    }

}
