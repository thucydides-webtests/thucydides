package net.thucydides.core.model;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.join;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.sum;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static net.thucydides.core.model.ReportNamer.ReportType.ROOT;
import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SUCCESS;
import static net.thucydides.core.util.NameConverter.withNoArguments;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.util.NameConverter;
import ch.lambdaj.function.convert.Converter;

import com.google.common.collect.ImmutableList;

/**
 * Represents the results of a test (or "scenario") execution. This
 * includes the narrative steps taken during the test, screenshots at each step,
 * the results of each step, and the overall result. A test scenario
 * can be associated with a user story using the UserStory annotation.
 *
 * @author johnsmart
 */
public class TestOutcome {

    /**
     * The name of the method implementing this test.
     */
    private final String methodName;

    /**
     *  The class containing the test method, if the test is implemented in a Java class.
     */
    private final Class<?> testCase;

    /**
     * The list of steps recorded in this test execution.
     * Each step can contain other nested steps.
     */
    private final List<TestStep> testSteps = new ArrayList<TestStep>();

    /**
     * A test can be linked to the user story it tests using the Story annotation.
     */
    private Story userStory;

    private String storedTitle;

    private long duration;

    private final long startTime;

    private Throwable testFailureCause;

    /**
     * Used to determine what result should be returned if there are no steps in this test.
     */
    private TestResult annotatedResult = null;
    /**
     * Keeps track of step groups.
     * If not empty, the top of the stack contains the step corresponding to the current step group - new steps should
     * be added here.
     */
    private Stack<TestStep> groupStack = new Stack<TestStep>();

    /**
     * The title is immutable once set. For convenience, you can create a test
     * run directly with a title using this constructor.
     */
    public TestOutcome(final String methodName) {
        this(methodName, null);
    }

    public TestOutcome(final String methodName, final Class<?> testCase) {
        startTime = System.currentTimeMillis();
        this.methodName = methodName;
        this.testCase = testCase;
        if (testCase != null) {
            initializeStoryFrom(testCase);
        }
    }

    /**
     * A test outcome should relate to a particular test class or user story class.
     */
    protected TestOutcome(final String methodName, final Class<?> testCase, final Story userStory) {
        startTime = System.currentTimeMillis();
        this.methodName = methodName;
        this.testCase = testCase;
        this.userStory = userStory;
    }

    /**
     * Create a new test outcome instance for a given test class or user story.
     */

    public static TestOutcome forTest(final String methodName, final Class<?> testCase) {
        return new TestOutcome(methodName, testCase);
    }

    private void initializeStoryFrom(final Class<?> testCase) {
        Story story;
        if (Story.testedInTestCase(testCase) != null) {
            story = Story.from(Story.testedInTestCase(testCase));
        } else {
            story = Story.from(testCase);
        }
        setUserStory(story);
    }

    /**
     * @return The name of the Java method implementing this test, if the test is implemented in Java.
     */
    public String getMethodName() {
        return methodName;
    }

    public static TestOutcome forTestInStory(final String testName, final Story story) {
        return new TestOutcome(testName, null, story);
    }

    public static TestOutcome forTestInStory(final String testName, final Story story, final Class<?> testClass) {
        if (story == null) {
            return new TestOutcome(testName, testClass);
        } else {
            return new TestOutcome(testName, testClass, story);
        }
    }


    @Override
    public String toString() {
        return join(extract(testSteps, on(TestStep.class).toString()));
    }

    /**
     * Return the human-readable name for this test.
     * This is derived from the test name for tests using a Java implementation, or can also be defined using
     * the Title annotation.
     * @return the human-readable name for this test.
     */
    public String getTitle() {
        if (storedTitle == null) {
            return obtainTitleFromAnnotationOrMethodName();
        } else {
            return storedTitle;
        }
    }

    private String obtainTitleFromAnnotationOrMethodName() {
        String annotatedTitle = TestAnnotations.forClass(testCase).getAnnotatedTitleForMethod(methodName);
        if (annotatedTitle != null) {
            return annotatedTitle;
        }
        return NameConverter.humanize(withNoArguments(methodName));
    }

    public String getStoryTitle() {
        return getTitleFrom(userStory);
    }

    private String getTitleFrom(final Story userStory) {
        return userStory.getName();
    }

    public String getReportName(final ReportNamer.ReportType type) {
        ReportNamer reportNamer = new ReportNamer(type);
        return reportNamer.getNormalizedTestNameFor(this);
    }

    public String getReportName(final ReportNamer.ReportType type, final String qualifier) {
        ReportNamer reportNamer = new ReportNamer(type);
        if (qualifier == null) {
            return reportNamer.getNormalizedTestNameFor(this);
        } else {
            return reportNamer.getNormalizedTestNameFor(this, qualifier);
        }
    }

    public String getReportName() {
        return getReportName(ROOT);
    }

    public String getScreenshotReportName() {
        return getReportName(ROOT) + "_screenshots";
    }

    /**
     * An acceptance test is made up of a series of steps. Each step is in fact
     * a small test, which follows on from the previous one. The outcome of the
     * acceptance test as a whole depends on the outcome of all of the steps.
     */
    public List<TestStep> getTestSteps() {
        return ImmutableList.copyOf(testSteps);
    }

    public List<Screenshot> getScreenshots() {
        List<Screenshot> screenshots = new ArrayList<Screenshot>();
        List<TestStep> testSteps = getFlattenedTestSteps();

        for(TestStep currentStep : testSteps) {
            if (currentStep.getScreenshot() != null) {
                screenshots.add(new Screenshot(currentStep.getScreenshot().getName(),
                                               currentStep.getDescription()));
            }
        }

        return ImmutableList.copyOf(screenshots);
    }

    public List<TestStep> getFlattenedTestSteps() {
        List<TestStep> flattenedTestSteps = new ArrayList<TestStep>();
        for (TestStep step : getTestSteps()) {
            flattenedTestSteps.add(step);
            if (step.isAGroup()) {
                flattenedTestSteps.addAll(step.getFlattenedSteps());
            }
        }
        return ImmutableList.copyOf(flattenedTestSteps);
    }

    public List<TestStep> getLeafTestSteps() {
        List<TestStep> leafTestSteps = new ArrayList<TestStep>();
        for (TestStep step : getTestSteps()) {
            if (step.isAGroup()) {
                leafTestSteps.addAll(step.getLeafTestSteps());
            } else {
                leafTestSteps.add(step);
            }

        }
        return ImmutableList.copyOf(leafTestSteps);
    }

    /**
     * The outcome of the acceptance test, based on the outcome of the test
     * steps. If any steps fail, the test as a whole is considered a failure. If
     * any steps are pending, the test as a whole is considered pending. If all
     * of the steps are ignored, the test will be considered 'ignored'. If all
     * of the tests succeed except the ignored tests, the test is a success.
     * The test result can also be overridden using the 'setResult()' method.
     */
    public TestResult getResult() {
        if (testFailureCause != null) {
            return FAILURE;
        }

        if (annotatedResult != null) {
            return annotatedResult;
        }

        TestResultList testResults = new TestResultList(getCurrentTestResults());
        return testResults.getOverallResult();
    }

    /**
     * Add a test step to this acceptance test.
     */
    public void recordStep(final TestStep step) {
        checkNotNull(step.getDescription(),
                "The test step description was not defined.");
        if (inGroup()) {
            getCurrentStepGroup().addChildStep(step);
        } else {
            testSteps.add(step);
        }
    }

    private TestStep getCurrentStepGroup() {
        return groupStack.peek();
    }

    private boolean inGroup() {
        return !groupStack.empty();
    }

    /**
     * Get the feature that includes the user story tested by this test.
     * If no user story is defined, no feature can be returned, so the method returns null.
     * If a user story has been defined without a class (for example, one that has been reloaded),
     * the feature will be built using the feature name and id in the user story.
     */
    public ApplicationFeature getFeature() {
        if (getUserStory() != null) {
            return obtainFeatureFromUserStory();
        }
        return null;
    }

    private ApplicationFeature obtainFeatureFromUserStory() {
        return getUserStory().getFeature();
    }

    public void setTitle(final String title) {
        this.storedTitle = title;
    }

    private List<TestResult> getCurrentTestResults() {
        return convert(testSteps, new ExtractTestResultsConverter());
    }

    /**
     * Creates a new step with this name and immediately turns it into a step group.
     * TODO: Review where this is used, as it is mainly for backward compatibility.
     */
    @Deprecated
    public void startGroup(final String groupName) {
        recordStep(new TestStep(groupName));
        startGroup();
    }


    /**
     * Turns the current step into a group. Subsequent steps will be added as children of the current step.
     */
    public void startGroup() {
        checkState(!testSteps.isEmpty());

        groupStack.push(getCurrentStep());
    }

    /**
     * Finish the current group. Subsequent steps will be added after the current step.
     */
    public void endGroup() {
        groupStack.pop();
    }

    /**
     * The current step is the last step in the step list, or the last step in the children of the current step group.
     */
    public TestStep getCurrentStep() {
        checkState(!testSteps.isEmpty());

        if (!inGroup()) {
            return lastStepIn(testSteps);
        } else {
            TestStep currentStepGroup = groupStack.peek();
            if (currentStepGroup.hasChildren()) {
                return lastStepIn(currentStepGroup.getChildren());
            } else {
                return currentStepGroup;
            }
        }

    }

    private TestStep lastStepIn(final List<TestStep> testSteps) {
        return testSteps.get(testSteps.size() - 1);
    }

    public TestStep getCurrentGroup() {
        checkState(inGroup());
        return groupStack.peek();
    }

    public void setUserStory(Story story) {
        this.userStory = story;
    }

    public void setTestFailureCause(Throwable cause) {
        this.testFailureCause = cause;
    }

    public Throwable getTestFailureCause() {
        return this.testFailureCause;
    }

    public void setAnnotatedResult(final TestResult annotatedResult) {
        this.annotatedResult = annotatedResult;
    }

    private static class ExtractTestResultsConverter implements Converter<TestStep, TestResult> {
        public TestResult convert(final TestStep step) {
            return step.getResult();
        }
    }

    public Integer getStepCount() {
        return testSteps.size();
    }

    public Integer getSuccessCount() {
        List<TestStep> allTestSteps = getLeafTestSteps();
        return select(allTestSteps, having(on(TestStep.class).isSuccessful())).size();
    }

    public Integer getFailureCount() {
        List<TestStep> allTestSteps = getLeafTestSteps();
        return select(allTestSteps, having(on(TestStep.class).isFailure())).size();
    }

    public Integer getIgnoredCount() {
        List<TestStep> allTestSteps = getLeafTestSteps();
        return select(allTestSteps, having(on(TestStep.class).isIgnored())).size();
    }

    public Integer getSkippedCount() {
        List<TestStep> allTestSteps = getLeafTestSteps();
        return select(allTestSteps, having(on(TestStep.class).isSkipped())).size();
    }

    public Integer getPendingCount() {
        List<TestStep> allTestSteps = getLeafTestSteps();
        return select(allTestSteps, having(on(TestStep.class).isPending())).size();
    }
    public Boolean isSuccess() {
        return (getResult() == SUCCESS);
    }

    public Boolean isFailure() {
        return (getResult() == FAILURE);
    }

    public Boolean isPending() {
        return (getResult() == PENDING);
    }

    public Story getUserStory() {
        return userStory;
    }

    public void recordDuration() {
        setDuration(System.currentTimeMillis() - startTime);
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        if ((duration == 0) && (testSteps != null) && (testSteps.size() > 0)) {
            return sum(testSteps, on(TestStep.class).getDuration());
        } else {
            return duration;
        }
    }

    public Integer countTestSteps() {
        return countLeafStepsIn(testSteps);
    }

    private Integer countLeafStepsIn(List<TestStep> testSteps) {
        int leafCount = 0;
        for(TestStep step : testSteps) {
            if (step.isAGroup()) {
                leafCount += countLeafStepsIn(step.getChildren());
            } else {
                leafCount++;
            }
        }
        return leafCount;
    }

}
