package net.thucydides.core.steps;
 
import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestStepGroup;
import net.thucydides.core.model.UserStory;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.screenshots.Photographer;
import net.thucydides.core.util.NameConverter;
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
 * Observations are recorded in an AcceptanceTestRun object. This includes
 * recording the names and results of each test, and taking and storing
 * screenshots at strategic points during the tests.
 *
 * @author johnsmart
 */
public class BaseStepListener implements StepListener {
 
    private final Collection<AcceptanceTestRun> acceptanceTestRuns;
    private AcceptanceTestRun currentAcceptanceTestRun;
    private ConcreteTestStep currentTestStep;
 
    private WebDriver driver;
    private File outputDirectory;
 
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseStepListener.class);
 
    private boolean aStepHasFailed;
    private Throwable stepError;

    private WebdriverProxyFactory proxyFactory;

    private BaseStepListener(final File outputDirectory) {
        this.proxyFactory = WebdriverProxyFactory.getFactory();
        this.acceptanceTestRuns = new ArrayList<AcceptanceTestRun>();
        this.outputDirectory = outputDirectory;
        aStepHasFailed = false;
        stepError = null;
    }

    protected WebdriverProxyFactory getProxyFactory() {
        return proxyFactory;
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
 
    public Throwable getStepError() {
        return stepError;
    }
 
    public List<AcceptanceTestRun> getTestRunResults() {
        return ImmutableList.copyOf(acceptanceTestRuns);
    }
 
    private void recordCurrentTestStep(final ExecutedStepDescription description) {
        if (currentTestStep != null) {
 
            addAnyTestedRequirementsIn(description);
 
            String testName = AnnotatedDescription.from(description).getName();
            getCurrentStep().setDescription(testName);
            getCurrentStep().recordDuration();
 
            getCurrentAcceptanceTestRun().recordStep(currentTestStep);
            getCurrentAcceptanceTestRun().recordDuration();
 
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
 
    protected AcceptanceTestRun getCurrentAcceptanceTestRun() {
        if (currentAcceptanceTestRun == null) {
            currentAcceptanceTestRun = new AcceptanceTestRun();
        }
        return currentAcceptanceTestRun;
    }
 
    protected void startNewCurrentAcceptanceTestRun() {
        if (currentAcceptanceTestRun != null) {
            acceptanceTestRuns.add(currentAcceptanceTestRun);
        }
        currentAcceptanceTestRun = null;
        aStepHasFailed = false;
        getCurrentAcceptanceTestRun();
    }
 
    public void testRunStarted(final String description) {
        testStarted(ExecutedStepDescription.withTitle(description));
    }
 
    public void testStarted(final ExecutedStepDescription description) {
        startNewCurrentAcceptanceTestRun();
        if (description.getTestMethod() != null) {
            getCurrentAcceptanceTestRun().setMethodName(description.getTestMethod().getName());
            getCurrentAcceptanceTestRun().setUserStory(withUserStoryFromTestCaseIn(description));
            updateTestRunRequirementsBasedOn(description);
        } else {
            getCurrentAcceptanceTestRun().setUserStory(withUserStoryFrom(description));
        }
 
        String annotatedTitle = AnnotatedDescription.from(description).getOptionalAnnotatedTitle();

        if (annotatedTitle != null) {
            getCurrentAcceptanceTestRun().setTitle(annotatedTitle);
        } else {
            setTitleIfNotAlreadySet();
        }
 
//        addAnyTestedRequirementsIn(description);
 
        acceptanceTestRuns.add(getCurrentAcceptanceTestRun());
    }
 
    private void addAnyTestedRequirementsIn(final ExecutedStepDescription description) {
        AnnotatedDescription testDescription = AnnotatedDescription.from(description);
        List<String> requirements = testDescription.getAnnotatedRequirements();
        for (String requirement : requirements) {
            currentTestStep.testsRequirement(requirement);
        }
    }
 
    private void updateTestRunRequirementsBasedOn(final ExecutedStepDescription description) {
        AnnotatedDescription testDescription = AnnotatedDescription.from(description);
        List<String> requirements = testDescription.getAnnotatedRequirements();
        for (String requirement : requirements) {
            getCurrentAcceptanceTestRun().testsRequirement(requirement);
        }
    }
 
    private void setTitleIfNotAlreadySet() {
        String testRunTitle;
        String methodName = getCurrentAcceptanceTestRun().getMethodName();
 
        if (methodName != null) {
            testRunTitle = NameConverter.humanize(methodName);
        } else {
            testRunTitle = getCurrentAcceptanceTestRun().getUserStory().getName();
 
        }
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
        String userStoryCode = AnnotatedDescription.from(description).getUserStoryCode();
        return new UserStory(name, userStoryCode, source);
    }
 
    private UserStory withUserStoryFrom(final ExecutedStepDescription description) {
        String name = NameConverter.humanize(description.getName());
        String userStoryCode = AnnotatedDescription.from(description).getUserStoryCode();
        return new UserStory(name, userStoryCode, "");
    }

    private void markCurrentTestAs(final TestResult result) {
        if (getCurrentStep() != null) {
            getCurrentStep().setResult(result);
        }
    }
 
    private TestStep getCurrentStep() {
        if (currentTestStep != null) {
            return currentTestStep;
        } else {
            return getCurrentAcceptanceTestRun().getCurrentGroup();
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
        if (getCurrentStep() != null) {
            String testName = aTestCalled(description);
            File screenshot = grabScreenshotFileFor(testName);
            getCurrentStep().setScreenshot(screenshot);
            if (screenshot != null) {
                File sourcecode = getPhotographer().getMatchingSourceCodeFor(screenshot);
                getCurrentStep().setHtmlSource(sourcecode);
            }
        }
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
            getCurrentAcceptanceTestRun().endGroup();
        } else {
            markCurrentTestAs(SUCCESS);
            takeScreenshotFor(description);
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
        stepFailedWith(failure);

        markCurrentTestAs(FAILURE);
        recordFailureDetailsInFailingTestStep(failure);
        takeScreenshotFor(failure.getDescription());
        if (currentTestStep != null) {
            recordCurrentTestStep(failure.getDescription());
        }
    }
 
    private void stepFailedWith(final StepFailure failure) {
        aStepHasFailed = true;
        stepError = failure.getException();
    }
 
    private boolean stepIsAGroup(final ExecutedStepDescription description) {
        return (description.isAGroup() || AnnotatedDescription.from(description).isAGroup());
    }
 
    public void stepIgnored(final ExecutedStepDescription description) {

        if (AnnotatedDescription.from(description).isPending()) {
            markCurrentTestAs(PENDING);
        } else if (AnnotatedDescription.from(description).isIgnored()) {
            ignoreStepMethodWith(description);
        } else if (aStepHasFailed()){
            markCurrentTestAs(SKIPPED);
        }
        if (currentTestStep != null) {
            recordCurrentTestStep(description);
        }
    }

    private void ignoreStepMethodWith(final ExecutedStepDescription description) {
        if (currentAcceptanceTestRun == null) {
            testStarted(description);
        }
        if (currentTestStep == null) {
            startNewTestStep(description);
        }
        markCurrentTestAs(IGNORED);
    }

 
    public void testFinished(final TestStepResult result) {
        currentAcceptanceTestRun = null;
    }
 
}