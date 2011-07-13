package net.thucydides.core.model;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.sum;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.thucydides.core.model.ReportNamer.ReportType.ROOT;
import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SUCCESS;
import static net.thucydides.core.util.NameConverter.withNoArguments;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.thucydides.core.annotations.Title;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.steps.TestDescription;
import net.thucydides.core.util.NameConverter;
import ch.lambdaj.function.convert.Converter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Represents the results of a test (or "scenario") execution. This
 * includes the narrative steps taken during the test, screenshots at each step,
 * the results of each step, and the overall result. A test scenario
 * can be associated with a user story using the UserStory annotation.
 *
 * @author johnsmart
 */
public class TestOutcome {

    private String methodName;

    private Story userStory;

    private Class<?> testCase;

    private long duration;

    private long startTime;

    private Set<String> testedRequirement = new HashSet<String>();

    private final List<TestStep> testSteps = new ArrayList<TestStep>();

    private final Stack<TestStepGroup> groupStack = new Stack<TestStepGroup>();

    /**
     * Create a new acceptance test run instance.
     */
    public TestOutcome() {
        startTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }

    /**
     * The title is immutable once set. For convenience, you can create a test
     * run directly with a title using this constructor.
     */
    public TestOutcome(final String methodName) {
        this();
        this.methodName = methodName;
    }

    /**
     * A test outcome should relate to a particular test class or user story class.
     */
    protected TestOutcome(final String scenarioName, final Story story, final Class<?> testCase) {
        this(scenarioName);
        this.testCase = testCase;
        recordRequirementsTestedBy(testCase, scenarioName);

        setUserStory(story);
    }

    /**
     * A test outcome should relate to a particular test class or user story class.
     */
    protected TestOutcome(final String scenarioName, final Class<?> testCase) {
        this(scenarioName);
        this.testCase = testCase;
        recordRequirementsTestedBy(testCase, scenarioName);

        initializeStoryFrom(testCase);

    }

    private void initializeStoryFrom(final Class<?> testCase) {
        Story story = Story.from(Story.testedInTestCase(testCase));
        setUserStory(story);
    }

    private void recordRequirementsTestedBy(final Class<?> testCase, final String scenarioName) {

        TestDescription testDescription = new TestDescription(testCase, scenarioName);
        if (testDescription.methodExists()) {
            testedRequirement.addAll(testDescription.getAnnotatedRequirements());
        }

    }

    /**
     * Create a new test outcome instance for a given test class or user story.
     */

    public static TestOutcome forTest(final String testName, final Class<?> testCase) {
        return new TestOutcome(testName, testCase);
    }

    public static TestOutcome forTestInStory(final String testName, final Story story) {
        return new TestOutcome(testName, story, null);
    }

    public static TestOutcome forTestInStory(final String testName, final Story story, final Class<?> testClass) {
        return new TestOutcome(testName, story, testClass);
    }

    public String getTitle() {
        String annotatedTitle = getAnnotatedTitleFor(methodName);
        if (annotatedTitle != null) {
            return annotatedTitle;
        }
        return NameConverter.humanize(withNoArguments(methodName));
    }

    private String getAnnotatedTitleFor(final String methodName) {
        String annotatedTitle = null;
        if (testCase != null) {
            if (currentTestCaseHasMethodCalled(methodName)) {
                Method testMethod = getMethodCalled(methodName);
                Title titleAnnotation = testMethod.getAnnotation(Title.class);
                if (titleAnnotation != null) {
                    annotatedTitle = titleAnnotation.value();
                }
            }
        }
        return annotatedTitle;
    }

    private boolean currentTestCaseHasMethodCalled(final String methodName) {
        return (getMethodCalled(methodName) != null);

    }

    private Method getMethodCalled(final String methodName) {
        String baseMethodName = withNoArguments(methodName);
        try {
            return testCase.getMethod(baseMethodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
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

    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void testsRequirement(final String requirement) {
        checkNotNull(requirement);
        testedRequirement.add(requirement);
    }

    public Set<String> getTestedRequirements() {
        return ImmutableSet.copyOf(testedRequirement);
    }

    /**
     * An acceptance test is made up of a series of steps. Each step is in fact
     * a small test, which follows on from the previous one. The outcome of the
     * acceptance test as a whole depends on the outcome of all of the steps.
     */
    public List<TestStep> getTestSteps() {
        return ImmutableList.copyOf(testSteps);
    }


    /**
     * The outcome of the acceptance test, based on the outcome of the test
     * steps. If any steps fail, the test as a whole is considered a failure. If
     * any steps are pending, the test as a whole is considered pending. If all
     * of the steps are ignored, the test will be considered 'ignored'. If all
     * of the tests succeed except the ignored tests, the test is a success.
     */
    public TestResult getResult() {
        TestResultList testResults = new TestResultList(getCurrentTestResults());
        return testResults.getOverallResult();
    }

    public void updateMostResultTestStepResult(final TestResult result) {
        if (testSteps.size() > 0) {
            testSteps.get(testSteps.size() - 1).setResult(result);
        }
    }


    /**
     * Add a test step to this acceptance test.
     */
    public void recordStep(final TestStep step) {
        checkNotNull(step.getDescription(),
                "The test step description was not defined.");

        if (groupStack.isEmpty()) {
            testSteps.add(step);
        } else {
            addStepToCurrentGroup(step);
        }
    }

    private void addStepToCurrentGroup(final TestStep step) {
        TestStepGroup group = groupStack.peek();
        group.addTestStep(step);
    }

    public void setDefaultGroupResult(final TestResult result) {
        if (!groupStack.isEmpty()) {
            TestStepGroup group = groupStack.peek();
            group.setDefaultResult(result);
        }

    }

    public TestStepGroup getCurrentGroup() {
        if (!groupStack.isEmpty()) {
            return groupStack.peek();
        } else {
            return null;
        }
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

private static class ExtractTestResultsConverter implements Converter<TestStep, TestResult> {
    public TestResult convert(final TestStep step) {
        return step.getResult();
    }

}

    private List<TestResult> getCurrentTestResults() {
        return convert(testSteps, new ExtractTestResultsConverter());
    }

    public Integer getStepCount() {
        return testSteps.size();
    }

    public Integer getSuccessCount() {
        List<TestStep> allTestSteps = getNestedTestSteps();
        return select(allTestSteps, having(on(TestStep.class).isSuccessful())).size();
    }

    private List<TestStep> getNestedTestSteps() {
        List<TestStep> allNestedTestSteps = new ArrayList<TestStep>();

        for (TestStep testStep : testSteps) {
            allNestedTestSteps.addAll(testStep.getFlattenedSteps());
        }
        return allNestedTestSteps;
    }

    public Integer getFailureCount() {
        List<TestStep> allTestSteps = getNestedTestSteps();
        return select(allTestSteps, having(on(TestStep.class).isFailure())).size();
    }

    public Integer getIgnoredCount() {
        List<TestStep> allTestSteps = getNestedTestSteps();
        return select(allTestSteps, having(on(TestStep.class).isIgnored())).size();
    }

    public Integer getSkippedCount() {
        List<TestStep> allTestSteps = getNestedTestSteps();
        return select(allTestSteps, having(on(TestStep.class).isSkipped())).size();
    }

    public Integer getPendingCount() {
        List<TestStep> allTestSteps = getNestedTestSteps();
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

    public Set<String> getAllTestedRequirements() {
        Set<String> allTestedRequirements = new HashSet<String>();
        allTestedRequirements.addAll(getTestedRequirements());
        for (TestStep step : getTestSteps()) {
            allTestedRequirements.addAll(step.getTestedRequirements());
        }
        return allTestedRequirements;
    }

    /**
     * Associate a user story with this test outcome.
     * Once a user story is set for a given test outcome, it should not be changed.
     */
    public void setUserStory(final Story userStory) {
        Preconditions.checkState(this.userStory == null);
        this.userStory = userStory;
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

    public void startGroup(final String description) {
        TestStepGroup newGroup = new TestStepGroup(description);

        if (currentlyInGroup()) {
            addStepToCurrentGroup(newGroup);
        } else {
            testSteps.add(newGroup);
        }

        groupStack.push(newGroup);

    }

    private boolean currentlyInGroup() {
        return !groupStack.isEmpty();
    }

    public void endGroup() {
        if (!groupStack.isEmpty()) {
            TestStepGroup group = groupStack.pop();
            group.recordDuration();
        }
    }

    public Integer countTestSteps() {
        return getNestedTestSteps().size();
    }

}
