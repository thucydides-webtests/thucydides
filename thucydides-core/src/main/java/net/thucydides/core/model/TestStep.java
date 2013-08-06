package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.join;
import static ch.lambdaj.Lambda.on;
import static net.thucydides.core.model.TestResult.ERROR;
import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;

/**
 * An acceptance test run is made up of test steps.
 * Test steps can be either concrete steps or groups of steps.
 * Each concrete step should represent an action by the user, and (generally) an expected outcome.
 * A test step is described by a narrative-style phrase (e.g. "the user clicks 
 * on the 'Search' button', "the user fills in the registration form', etc.).
 * A screenshot is stored for each step.
 * 
 * @author johnsmart
 *
 */
public class TestStep {

    private String description;    
    private long duration;
    private long startTime;
    private List<ScreenshotAndHtmlSource> screenshots = new ArrayList<ScreenshotAndHtmlSource>();
    private Throwable cause;
    private TestResult result;

    private List<TestStep> children = new ArrayList<TestStep>();

    public TestStep() {
        startTime = now().getMillis();
    }

    private SystemClock getSystemClock() {
        return Injectors.getInjector().getInstance(SystemClock.class);
    }

    private DateTime now() {
        return getSystemClock().getCurrentTime();
    }

    public static TestStepBuilder forStepCalled(String description) {
        return new TestStepBuilder(description);
    }

    public boolean hasScreenshots() {
        return !getScreenshots().isEmpty();
    }

    protected List<TestStep> children() {
        return children;
    }

    public static class TestStepBuilder {
        private final String description;

        public TestStepBuilder(String description) {
            this.description = description;
        }

        public TestStep withResult(TestResult result) {
            TestStep step = new TestStep(description);
            step.setResult(result);
            return step;
        }
    }
    @Override
    public String toString() {
        if (!hasChildren()) {
            return description;
        } else {
            String childDescriptions = join(extract(children, on(TestStep.class).toString()));
            return description + " [" + childDescriptions + "]";
        }
    }

    public TestStep(final String description) {
        this();
        this.description = description;
    }

    public TestStep(final DateTime startTime, final String description) {
        this();
        this.startTime = startTime.getMillis();
        this.description = description;
    }

    public TestStep startingAt(DateTime time) {
        TestStep newTestStep = new TestStep();
        newTestStep.description = description;
        newTestStep.startTime = time.getMillis();
        newTestStep.duration = duration;
        newTestStep.screenshots = new ArrayList(screenshots);
        newTestStep.cause = cause;
        newTestStep.result = result;
        return newTestStep;
    }


    public void recordDuration() {
        setDuration(now().getMillis() - startTime);
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public List<TestStep> getChildren() {
        return ImmutableList.copyOf(children);
    }

    public List<ScreenshotAndHtmlSource> getScreenshots() {
        return ImmutableList.copyOf(screenshots);
    }



    public ScreenshotAndHtmlSource getFirstScreenshot() {
        if ((screenshots != null) && (!screenshots.isEmpty())) {
            return screenshots.get(0);
        } else {
            return null;
        }
    }

    public boolean needsScreenshots() {
        return (!isAGroup() && getScreenshots() != null);
    }
    /**
     * Each test step has a result, indicating the outcome of this step.
     * @param result The test outcome associated with this step.
     */
    public void setResult(final TestResult result) {
        this.result = result;
    }

    public TestResult getResult() {
        if (isAGroup() && !groupResultOverridesChildren()) {
            return getResultFromChildren();
        } else {
            return getResultFromThisStep();
        }
    }

    private TestResult getResultFromThisStep() {
        if (result != null) {
            return result;
        } else {
            return TestResult.PENDING;
        }
    }

    private boolean groupResultOverridesChildren() {
        return ((result == SKIPPED) || (result == IGNORED) || (result == PENDING));
    }

    private TestResult getResultFromChildren() {
        TestResultList resultList = TestResultList.of(getChildResults());
        return resultList.getOverallResult();
    }

    private List<TestResult> getChildResults() {
        List<TestResult> results = new ArrayList<TestResult>();
        for (TestStep step : getChildren()) {
            results.add(step.getResult());
        }
        return results;
    }

    public Boolean isSuccessful() {
        return getResult() == SUCCESS;
    }

    public Boolean isFailure() {
        return  getResult() == FAILURE;
    }

    public Boolean isError() {
        return getResult() == ERROR;
    }

    public Boolean isIgnored() {
        return  getResult() == IGNORED;
    }

    public Boolean isSkipped() {
        return  getResult() == SKIPPED;
    }

    public Boolean isPending() {
        return  getResult() == PENDING;
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public double getDurationInSeconds() {
        return TestDuration.of(duration).inSeconds();
    }

    /**
     * Indicate that this step failed with a given error.
     * @param exception why the test failed.
     */
    public void failedWith(final Throwable exception) {
        setResult(new FailureAnalysis().resultFor(exception));
        this.cause = exception;
    }

    public String getErrorMessage() {
        return (cause != null) ? errorMessageFrom(cause) : "";
    }

    /**
     * The test has been aborted (marked as pending or ignored) for a reason described in the exception.
     * @param exception
     */
    public void testAborted(final Throwable exception) {
        this.cause = exception;
    }

    private String errorMessageFrom(final Throwable error) {
        return (error.getCause() != null) ? error.getCause().getMessage() : error.getMessage();
    }

    public String getShortErrorMessage() {
        return new ErrorMessageFormatter(getErrorMessage()).getShortErrorMessage();
    }

    public Throwable getException() {
        return cause;
    }

    public List<? extends TestStep> getFlattenedSteps() {
        List<TestStep> flattenedSteps = new ArrayList<TestStep>();
        for(TestStep child : getChildren()) {
            flattenedSteps.add(child);
            if (child.isAGroup()) {
                flattenedSteps.addAll(child.getFlattenedSteps());
            }
        }
        return flattenedSteps;
    }
    
    public boolean isAGroup() {
        return hasChildren();
    }

    public void addChildStep(final TestStep step) {
        children.add(step);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public Collection<? extends TestStep> getLeafTestSteps() {
        List<TestStep> leafSteps = new ArrayList<TestStep>();
        for(TestStep child : getChildren()) {
            if (child.isAGroup()) {
                leafSteps.addAll(child.getLeafTestSteps());
            } else {
                leafSteps.add(child);
            }
        }
        return leafSteps;
    }

    public void addScreenshot(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        if (thisIsANew(screenshotAndHtmlSource)) {
            screenshots.add(screenshotAndHtmlSource);
        }
    }

    private boolean thisIsANew(ScreenshotAndHtmlSource screenshotAndHtmlSource) {
        if (screenshots.isEmpty()) {
            return true;    
        } else {
            ScreenshotAndHtmlSource latestScreenshotAndHtmlSource = screenshots.get(screenshots.size() - 1);
            return !latestScreenshotAndHtmlSource.equals(screenshotAndHtmlSource);
        }
    }

    public int getScreenshotCount() {
        return screenshots.size();
    }

    public void removeScreenshot(int index) {
        screenshots.remove(index);
    }
}
