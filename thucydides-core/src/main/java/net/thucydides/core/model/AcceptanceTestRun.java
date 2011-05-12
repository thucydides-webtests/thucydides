package net.thucydides.core.model;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static net.thucydides.core.model.ReportNamer.ReportType.ROOT;
import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SUCCESS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;


import ch.lambdaj.function.convert.Converter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.thoughtworks.xstream.XStream;
import net.thucydides.core.reports.xml.AcceptanceTestRunConverter;

/**
 * Represents the results of an acceptance test (or "scenario") execution. This
 * includes the narrative steps taken during the test, screenshots at each step,
 * the results of each step, and the overall result. An Acceptance test scenario
 * can be associated with a UserStory using the UserStory annotation.
 * 
 * @author johnsmart
 * 
 */
public class AcceptanceTestRun {

    private String title;

    private String methodName;
    
    private UserStory userStory;

    private long duration;

    private long startTime;
    
    private Set<String> testedRequirement = new HashSet<String>();

    private final List<TestStep> testSteps = new ArrayList<TestStep>();

    private final Stack<TestStepGroup> groupStack = new Stack<TestStepGroup>();
    
    /**
     * Create a new acceptance test run instance.
     */
    public AcceptanceTestRun() {
        startTime = System.currentTimeMillis();
    }

    /**
     * The title is immutable once set. For convenience, you can create a test
     * run directly with a title using this constructor.
     */
    public AcceptanceTestRun(final String title) {
        this();
        this.title = title;
        this.methodName = normalizedFormOf(title);
    }

    /**
     * The test case title.
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    public String toXML() {
        XStream xstream = new XStream();
        xstream.alias("acceptance-test-run", AcceptanceTestRun.class);
        xstream.registerConverter(new AcceptanceTestRunConverter());
        return xstream.toXML(this);
    }
    /**
     * An acceptance test run always has a title. The title should be something
     * like the name of the user story being tested, possibly with some
     * precisions if several test cases test the same user story. If the test
     * cases are written using a BDD style, the name can be derived directly
     * from the test case name.
     */
    public String getTitle() {
        return title;
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
    
    private String normalizedFormOf(final String name) {
        return name.replaceAll("\\s", "_").toLowerCase(Locale.getDefault());
    }

    public void testsRequirement(final String requirement) {
        Preconditions.checkNotNull(requirement);
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
        Preconditions.checkNotNull(step.getDescription(),
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

    public void setDefaultGroupResult(TestResult result) {
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

    public void setUserStory(final UserStory userStory) {
        this.userStory = userStory;
    }

    public UserStory getUserStory() {
        return userStory;
    }

    public void recordDuration() {
        setDuration(System.currentTimeMillis() - startTime);
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
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

/*
     public TestResult getResultForGroup(final String group) {

        TestResultList testResults = new TestResultList(getTestResultsInGroup(group));
        return testResults.getOverallResult();
    }

    private List<TestResult> getTestResultsInGroup(final String group) {
        List<TestResult> results = new ArrayList<TestResult>();
        for(TestStep step : getTestSteps()) {
            if (step.isInGroup(group)) {
                results.add(step.getResult());
            }
        }
        return results;
    }
    */

}
