package net.thucydides.core.model;

import ch.lambdaj.function.convert.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.TestAnnotations;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.images.SimpleImageInfo;
import net.thucydides.core.issues.IssueTracking;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.reports.html.Formatter;
import net.thucydides.core.reports.saucelabs.LinkGenerator;
import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.core.statistics.model.TestStatistics;
import net.thucydides.core.statistics.service.TagProvider;
import net.thucydides.core.statistics.service.TagProviderService;
import net.thucydides.core.steps.StepFailure;
import net.thucydides.core.steps.StepFailureException;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.NameConverter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.join;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.sort;
import static ch.lambdaj.Lambda.sum;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static net.thucydides.core.model.ReportType.HTML;
import static net.thucydides.core.model.ReportType.ROOT;
import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;
import static net.thucydides.core.util.NameConverter.withNoArguments;

/**
 * Represents the results of a test (or "scenario") execution. This
 * includes the narrative steps taken during the test, screenshots at each step,
 * the results of each step, and the overall result. A test scenario
 * can be associated with a user story using the UserStory annotation.
 *
 * A TestOutcome is stored as an XML file after a test is executed. When the aggregate reports
 * are generated, the test outcome XML files are loaded into memory and processed.
 *
 * @author johnsmart
 */
public class TestOutcome {

    private static final int RECENT_TEST_RUN_COUNT = 10;
    /**
     * The name of the method implementing this test.
     */
    private final String methodName;

    /**
     * The class containing the test method, if the test is implemented in a Java class.
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

    private Set<String> issues;
    private Set<String> additionalIssues;
    private Set<TestTag> tags;

    private long duration;

    private long startTime;

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

    private IssueTracking issueTracking;

    private EnvironmentVariables environmentVariables;

    /**
     * The session ID for this test, is a remote web driver was used.
     * If the tests are run on SauceLabs, this is used to generate a link to the corresponding report and video.
     */
    private String sessionId;

    private LinkGenerator linkGenerator;

    /**
     * Test statistics, read from the statistics database.
     * This data is only loaded when required, and added to the TestOutcome using the corresponding setter.
     */
    private TestStatistics statistics;

    /**
     * Returns a set of tag provider classes that are used to determine the tags to associate with a test outcome.
     */
    private TagProviderService tagProviderService;

    /**
     * An optional qualifier used to distinguish different runs of this test in data-driven tests.
     */
    private Optional<String> qualifier;
    /**
     * The title is immutable once set. For convenience, you can create a test
     * run directly with a title using this constructor.
     * @param methodName The name of the Java method that implements this test.
     */

    private DataTable dataTable;

    public TestOutcome(final String methodName) {
        this(methodName, null);
    }

    public TestOutcome(final String methodName, final Class<?> testCase) {
        startTime = System.currentTimeMillis();
        this.methodName = methodName;
        this.testCase = testCase;
        this.additionalIssues = new HashSet<String>();
        this.issueTracking = Injectors.getInjector().getInstance(IssueTracking.class);
        this.linkGenerator = Injectors.getInjector().getInstance(LinkGenerator.class);
        this.qualifier = Optional.absent();
        if (testCase != null) {
            initializeStoryFrom(testCase);
        }
    }

    private TagProviderService getTagProviderService() {
        if (tagProviderService == null) {
            tagProviderService = Injectors.getInjector().getInstance(TagProviderService.class);
        }
        return tagProviderService;
    }

    public TestOutcome usingIssueTracking(IssueTracking issueTracking) {
        this.issueTracking = issueTracking;
        return this;
    }

    public void setEnvironmentVariables(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public EnvironmentVariables getEnvironmentVariables() {
        if (environmentVariables == null) {
            environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        }
        return environmentVariables;
    }

    /**
     * A test outcome should relate to a particular test class or user story class.
     * @param methodName The name of the Java method implementing this test, if the test is a JUnit or TestNG test (for example)
     * @param testCase The test class that contains this test method, if the test is a JUnit or TestNG test
     * @param userStory If the test is not implemented by a Java class (e.g. an easyb story), we may just use the Story class to
     *                  represent the story in which the test is implemented.
     */
    protected TestOutcome(final String methodName, final Class<?> testCase, final Story userStory) {
        startTime = System.currentTimeMillis();
        this.methodName = methodName;
        this.testCase = testCase;
        this.additionalIssues = new HashSet<String>();
        this.userStory = userStory;
        this.issueTracking = Injectors.getInjector().getInstance(IssueTracking.class);
        this.linkGenerator = Injectors.getInjector().getInstance(LinkGenerator.class);
    }

    protected TestOutcome(final long startTime,
                          final long duration,
                          final String title,
                          final String methodName,
                          final Class<?> testCase,
                          final List<TestStep> testSteps,
                          final Set<String> issues,
                          final Set<String> additionalIssues,
                          final Set<TestTag> tags,
                          final Story userStory,
                          final Throwable testFailureCause,
                          final TestResult annotatedResult,
                          final Optional<String> qualifier) {
        this.startTime = startTime;
        this.duration = duration;
        this.storedTitle = title;
        this.methodName = methodName;
        this.testCase = testCase;
        this.testSteps.addAll(testSteps);
        this.issues = issues;
        this.additionalIssues = additionalIssues;
        this.tags = tags;
        this.userStory = userStory;
        this.testFailureCause = testFailureCause;
        this.qualifier = qualifier;
        this.annotatedResult = annotatedResult;
        this.issueTracking = Injectors.getInjector().getInstance(IssueTracking.class);
        this.linkGenerator = Injectors.getInjector().getInstance(LinkGenerator.class);
    }

    /**
     * Create a new test outcome instance for a given test class or user story.
     * @param methodName  The name of the Java method implementing this test,
     * @param testCase The  JUnit or TestNG test class that contains this test method
     * @return A new TestOutcome object for this test.
     */
    public static TestOutcome forTest(final String methodName, final Class<?> testCase) {
        return new TestOutcome(methodName, testCase);
    }

    public TestOutcome withQualifier(String qualifier) {
        if (qualifier != null) {
            return new TestOutcome(this.startTime,
                                   this.duration,
                                   this.storedTitle,
                                   this.methodName,
                                   this.testCase,
                                   this.testSteps,
                                   this.issues,
                                   this.additionalIssues,
                                   this.tags,
                                   this.userStory,
                                   this.testFailureCause,
                                   this.annotatedResult,
                                   Optional.fromNullable(qualifier));
        } else {
            return this;
        }
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

    public static TestOutcome forTestInStory(final String testName, final Class<?> testCase, final Story story) {
        return new TestOutcome(testName, testCase, story);
    }

    @Override
    public String toString() {
        return getTitle() + ":" + join(extract(testSteps, on(TestStep.class).toString()));
    }

    /**
     * Return the human-readable name for this test.
     * This is derived from the test name for tests using a Java implementation, or can also be defined using
     * the Title annotation.
     *
     * @return the human-readable name for this test.
     */
    public String getTitle() {
        if (storedTitle == null) {
            return obtainTitleFromAnnotationOrMethodName();
        } else {
            return storedTitle;
        }
    }

    public String getTitleWithLinks() {
        return getFormatter().addLinks(getTitle());
    }

    private Formatter getFormatter() {
        return new Formatter(issueTracking);
    }

    private String obtainTitleFromAnnotationOrMethodName() {
        Optional<String> annotatedTitle = TestAnnotations.forClass(testCase).getAnnotatedTitleForMethod(methodName);
        String rootTitle = annotatedTitle.or(NameConverter.humanize(withNoArguments(methodName)));
        if ((qualifier != null) && (qualifier.isPresent())) {
            return qualified(rootTitle);
        } else {
            return rootTitle;
        }

    }

    private String qualified(String rootTitle) {
        return rootTitle + " [" + qualifier.get() + "]";
    }

    public String getStoryTitle() {
        return getTitleFrom(userStory);
    }

    public String getPath() {
        if (userStory != null) {
            return userStory.getPath();
        } else {
            return null;
        }
    }

    private String getTitleFrom(final Story userStory) {
        return userStory.getName();
    }

    public String getReportName(final ReportType type) {
        return ReportNamer.forReportType(type).getNormalizedTestNameFor(this);
    }

    public String getSimpleReportName(final ReportType type) {
        ReportNamer reportNamer = ReportNamer.forReportType(type);
        return reportNamer.getSimpleTestNameFor(this);
    }

    public String getHtmlReport() {
        return getReportName(HTML);
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
     * @return A list of top-level test steps for this test.
     */
    public List<TestStep> getTestSteps() {
        return ImmutableList.copyOf(testSteps);
    }

    public List<Screenshot> getScreenshots() {
        List<Screenshot> screenshots = new ArrayList<Screenshot>();

        List<TestStep> testStepsWithScreenshots = select(getFlattenedTestSteps(),
                                                         having(on(TestStep.class).needsScreenshots()));

        for (TestStep currentStep : testStepsWithScreenshots) {
            screenshots.addAll(screenshotsIn(currentStep));
        }

        return ImmutableList.copyOf(screenshots);
    }

    private List<Screenshot> screenshotsIn(TestStep currentStep) {
        return convert(currentStep.getScreenshots(), toScreenshotsFor(currentStep));
    }

    private Converter<ScreenshotAndHtmlSource, Screenshot> toScreenshotsFor(final TestStep currentStep) {
        return new Converter<ScreenshotAndHtmlSource, Screenshot>() {
            public Screenshot convert(ScreenshotAndHtmlSource from) {
                return new Screenshot(from.getScreenshotFile().getName(),
                                      currentStep.getDescription(),
                                      widthOf(from.getScreenshotFile()),
                                      currentStep.getException());
            }
        };
    }

    private int widthOf(final File screenshot) {
        try {
            return new SimpleImageInfo(screenshot).getWidth();
        } catch (IOException e) {
            return ThucydidesSystemProperty.DEFAULT_WIDTH;
        }
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
     * @return The outcome of this test.
     */
    public TestResult getResult() {
        if (testFailureCause != null) {
            return FAILURE;
        }

        if (annotatedResult != null) {
            return annotatedResult;
        }

        TestResultList testResults = TestResultList.of(getCurrentTestResults());
        return testResults.getOverallResult();
    }

    /**
     * Add a test step to this acceptance test.
     * @param step a completed step to be added to this test outcome.
     * @return this TestOucome insstance - this is a convenience to allow method chaining.
     */
    public TestOutcome recordStep(final TestStep step) {
        checkNotNull(step.getDescription(), "The test step description was not defined.");
        if (inGroup()) {
            getCurrentStepGroup().addChildStep(step);
        } else {
            testSteps.add(step);
        }
        return this;
    }

    public TestOutcome withStep(final TestStep step) {
        return recordStep(step);
    }

    public TestOutcome andStep(final TestStep step) {
        return recordStep(step);
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
     * @return The Feature defined for this TestOutcome, if any
     */
    public ApplicationFeature getFeature() {
        if ((getUserStory() != null) && (getUserStory().getFeature() != null)) {
            return getUserStory().getFeature();
        } else {
            return null;
        }
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

    public Optional<String> getQualifier() {
        return qualifier;
    }

    /**
     * Turns the current step into a group. Subsequent steps will be added as children of the current step.
     */
    public void startGroup() {
        if (!testSteps.isEmpty()) {
            groupStack.push(getCurrentStep());
        }
    }

    /**
     * Finish the current group. Subsequent steps will be added after the current step.
     */
    public void endGroup() {
        if (!groupStack.isEmpty()) {
            groupStack.pop();
        }
    }

    /**
     * @return The current step is the last step in the step list, or the last step in the children of the current step group.
     */
    public TestStep getCurrentStep() {
        checkState(!testSteps.isEmpty());

        if (!inGroup()) {
            return lastStepIn(testSteps);
        } else {
            TestStep currentStepGroup = groupStack.peek();
            return lastStepIn(currentStepGroup.getChildren());
        }

    }

    public TestStep getLastStep() {
        checkState(!testSteps.isEmpty());

        if (!inGroup()) {
            return lastStepIn(testSteps);
        } else {
            TestStep currentStepGroup = groupStack.peek();
            return lastStepIn(currentStepGroup.getChildren());
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

    private Set<String> issues() {
        if (!thereAre(issues)) {
            issues = readIssues();
        }
        return issues;
    }

    public Set<String> getIssues() {
        Set<String> allIssues = new HashSet<String>(issues());
        if (thereAre(additionalIssues)) {
            allIssues.addAll(additionalIssues);
        }
        return allIssues;
    }

    public Class<?> getTestCase() {
        return testCase;
    }

    private boolean thereAre(Set<String> anyIssues) {
        return ((anyIssues != null) && (!anyIssues.isEmpty()));
    }

    public void addIssues(List<String> issues) {
        additionalIssues.addAll(issues);
    }

    private Set<String> readIssues() {
        Set<String> taggedIssues = new HashSet<String>();
        if (testCase != null) {
            addMethodLevelIssuesTo(taggedIssues);
            addClassLevelIssuesTo(taggedIssues);
        }
        addTitleLevelIssuesTo(taggedIssues);
        return taggedIssues;
    }

    private void addClassLevelIssuesTo(Set<String> issues) {
        String classIssue = TestAnnotations.forClass(testCase).getAnnotatedIssueForTestCase(testCase);
        if (classIssue != null) {
            issues.add(classIssue);
        }
        String[] classIssues = TestAnnotations.forClass(testCase).getAnnotatedIssuesForTestCase(testCase);
        if (classIssues != null) {
            issues.addAll(Arrays.asList(classIssues));
        }
    }

    private void addMethodLevelIssuesTo(Set<String> issues) {
        Optional<String> issue = TestAnnotations.forClass(testCase).getAnnotatedIssueForMethod(getMethodName());
        if (issue.isPresent()) {
            issues.add(issue.get());
        }
        String[] multipleIssues = TestAnnotations.forClass(testCase).getAnnotatedIssuesForMethod(getMethodName());
        issues.addAll(Arrays.asList(multipleIssues));
    }

    private void addTitleLevelIssuesTo(Set<String> issues) {
        List<String> titleIssues = Formatter.issuesIn(getTitle());
        if (!titleIssues.isEmpty()) {
            issues.addAll(titleIssues);
        }
    }

    public String getFormattedIssues() {
        Set<String> issues = getIssues();
        if (!issues.isEmpty()) {
            List<String> orderedIssues = sort(getIssues(), on(String.class));
            return "(" + getFormatter().addLinks(StringUtils.join(orderedIssues, ", ")) + ")";
        } else {
            return "";
        }
    }

    public void isRelatedToIssue(String issue) {
        issues().add(issue);
    }

    public void lastStepFailedWith(StepFailure failure) {
        setTestFailureCause(failure.getException());
        TestStep lastTestStep = testSteps.get(testSteps.size() - 1);
        lastTestStep.failedWith(new StepFailureException(failure.getMessage(), failure.getException()));
    }

    public Set<TestTag> getTags() {
        if (tags == null) {
            tags = getTagsUsingTagProviders(getTagProviderService().getTagProviders());
        }
        return ImmutableSet.copyOf(tags);
    }

    private Set<TestTag> getTagsUsingTagProviders(List<TagProvider> tagProviders) {
        Set<TestTag> tags  = Sets.newHashSet();
        for (TagProvider tagProvider : tagProviders) {
            Set<TestTag> providedTags = tagProvider.getTagsFor(this);
            if (providedTags != null) {
                tags.addAll(tagProvider.getTagsFor(this));
            }
        }
        return tags;
    }

    public void setTags(Set<TestTag> tags) {
        this.tags = Sets.newHashSet(tags);
    }


    public void addTags(List<TestTag> tags) {
        getTags();
        this.tags.addAll(tags);
    }

    public List<String> getIssueKeys() {
        return convert(getIssues(), toIssueKeys());
    }

    private Converter<String, String> toIssueKeys() {
        return new Converter<String,String>() {

            public String convert(String issueNumber) {
                String issueKey = issueNumber;
                if (issueKey.startsWith("#")) {
                    issueKey = issueKey.substring(1);
                }
                if (StringUtils.isNumeric(issueKey) && (getProjectPrefix() != null)) {
                    Joiner joiner = Joiner.on("-");
                    issueKey = joiner.join(getProjectPrefix(), issueKey);
                }
                return issueKey;
            }


        };
    }

    private String getProjectPrefix() {
        return ThucydidesSystemProperty.PROJECT_KEY.from(getEnvironmentVariables());
    }

    public String getQualifiedMethodName() {
        if ((qualifier != null) && (qualifier.isPresent())) {
            String qualifierWithoutSpaces = qualifier.get().replaceAll(" ", "_");
            return getMethodName() + "_" + qualifierWithoutSpaces;
        } else {
            return getMethodName();
        }
    }

    public static TestOutcome emptyCopyOf(TestOutcome baseTestOutcome) {
        TestOutcome newTestOutcome = new TestOutcome(baseTestOutcome.methodName, baseTestOutcome.testCase);
        newTestOutcome.additionalIssues = ImmutableSet.copyOf(baseTestOutcome.additionalIssues);
        newTestOutcome.annotatedResult = baseTestOutcome.annotatedResult;
        newTestOutcome.duration = baseTestOutcome.duration;
        if (baseTestOutcome.issues != null) {
            newTestOutcome.issues = ImmutableSet.copyOf(baseTestOutcome.issues);
        }
        newTestOutcome.issueTracking = baseTestOutcome.issueTracking;
        newTestOutcome.linkGenerator = baseTestOutcome.linkGenerator;
        newTestOutcome.qualifier = baseTestOutcome.qualifier;
        newTestOutcome.sessionId = baseTestOutcome.sessionId;
        newTestOutcome.startTime = baseTestOutcome.startTime;
        newTestOutcome.statistics = baseTestOutcome.statistics;
        newTestOutcome.storedTitle = baseTestOutcome.storedTitle;
        newTestOutcome.tagProviderService = baseTestOutcome.tagProviderService;
        if (baseTestOutcome.tags != null) {
            newTestOutcome.tags = ImmutableSet.copyOf(baseTestOutcome.tags);
        }
        newTestOutcome.testFailureCause = baseTestOutcome.testFailureCause;
        return newTestOutcome;
    }

    /**
     * Returns the name of the test prefixed by the name of the story.
     */
    public String getCompleteName() {
        if (StringUtils.isNotEmpty(getStoryTitle())) {
            return getStoryTitle() + ":" + getMethodName();
        } else {
            return getTestCase() + ":" + getMethodName();
        }
    }

    public void useExamplesFrom(DataTable table) {
        this.dataTable = table;
    }

    public void moveToNextRow() {
        if (!dataTable.atLastRow()) {
            dataTable.nextRow();
        }
    }

    public void updateCurrentRowResult(TestResult result) {
        dataTable.currentRow().hasResult(result);
    }

    public boolean dataIsPredefined() {
        return dataTable.hasPredefinedRows();
    }

    public void addRow(Map<String, String> data) {
        dataTable.addRow(data);
    }

    private static class ExtractTestResultsConverter implements Converter<TestStep, TestResult> {
        public TestResult convert(final TestStep step) {
            return step.getResult();
        }
    }

    public Integer getStepCount() {
        return testSteps.size();
    }

    public Integer getNestedStepCount() {
        return getFlattenedTestSteps().size();
    }

    public Integer getSuccessCount() {
        return count(successfulSteps()).in(getLeafTestSteps());
    }

    public Integer getFailureCount() {
        return count(failingSteps()).in(getLeafTestSteps());
    }

    public Integer getIgnoredCount() {
        return count(ignoredSteps()).in(getLeafTestSteps());
    }

    public Integer getSkippedOrIgnoredCount() {
        return getIgnoredCount() + getSkippedCount();
    }

    public Integer getSkippedCount() {
        return count(skippedSteps()).in(getLeafTestSteps());
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
        return ((getResult() == PENDING) || (getStepCount() == 0));
    }

    public Boolean isSkipped() {
        return (getResult() == SKIPPED) || (getResult() == IGNORED);
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

    public Long getDuration() {
        if ((duration == 0) && (testSteps != null) && (testSteps.size() > 0)) {
            return sum(testSteps, on(TestStep.class).getDuration());
        } else {
            return duration;
        }
    }

    /**
     * @return The total duration of all of the tests in this set in milliseconds.
     */
    public double getDurationInSeconds() {
        return TestDuration.of(duration).inSeconds();
    }

    /**
     * Returns the link to the associated video (e.g. from Saucelabs) for this test.
     * @return a URL.
     */
    public String getVideoLink() {
        return linkGenerator.linkFor(this);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    StepCountBuilder count(StepFilter filter) {
        return new StepCountBuilder(filter);
    }

    public static class StepCountBuilder {
        private final StepFilter filter;

        public StepCountBuilder(StepFilter filter) {
            this.filter = filter;
        }

        int in(List<TestStep> steps) {
            int count = 0;
            for (TestStep step : steps) {
                if (filter.apply(step)) {
                    count++;
                }
            }
            return count;
        }
    }


    public Integer countTestSteps() {
        return countLeafStepsIn(testSteps);
    }

    private Integer countLeafStepsIn(List<TestStep> testSteps) {
        int leafCount = 0;
        for (TestStep step : testSteps) {
            if (step.isAGroup()) {
                leafCount += countLeafStepsIn(step.getChildren());
            } else {
                leafCount++;
            }
        }
        return leafCount;
    }

    abstract class StepFilter {
        abstract boolean apply(TestStep step);

    }

    StepFilter successfulSteps() {
        return new StepFilter() {
            @Override
            boolean apply(TestStep step) {
                return step.isSuccessful();
            }
        };
    }

    StepFilter failingSteps() {
        return new StepFilter() {
            @Override
            boolean apply(TestStep step) {
                return step.isFailure();
            }
        };
    }

    StepFilter ignoredSteps() {
        return new StepFilter() {
            @Override
            boolean apply(TestStep step) {
                return step.isIgnored();
            }
        };
    }

    StepFilter skippedSteps() {
        return new StepFilter() {
            @Override
            boolean apply(TestStep step) {
                return step.isSkipped();
            }
        };
    }

    public void setStatistics(TestStatistics statistics) {
        this.statistics = statistics;
    }

    public TestStatistics getStatistics() {
        return statistics;
    }

    public double getOverallStability() {
        if (getStatistics() == null) return 0.0;
        return getStatistics().getOverallPassRate();
    }

    public double getRecentStability() {
        if (getStatistics() == null) return 0.0;
        return getStatistics().getPassRate().overTheLast(RECENT_TEST_RUN_COUNT).testRuns();
    }

    public Long getRecentTestRunCount() {
        if (getStatistics() == null) return 0L;
        return (getStatistics().getTotalTestRuns() > RECENT_TEST_RUN_COUNT) ? RECENT_TEST_RUN_COUNT :  getStatistics().getTotalTestRuns();
    }

    public int getRecentPassCount() {
        if (getStatistics() == null) return 0;
        return getStatistics().countResults().overTheLast(RECENT_TEST_RUN_COUNT).whereTheOutcomeWas(TestResult.SUCCESS);
    }

    public int getRecentFailCount() {
        if (getStatistics() == null) return 0;
        return getStatistics().countResults().overTheLast(RECENT_TEST_RUN_COUNT).whereTheOutcomeWas(TestResult.FAILURE);
    }

    public int getRecentPendingCount() {
        if (getStatistics() == null) return 0;
        return getStatistics().countResults().overTheLast(RECENT_TEST_RUN_COUNT).whereTheOutcomeWas(TestResult.PENDING);
    }

    public DateTime getStartTime() {
        return new DateTime(startTime);
    }

    public boolean isDataDriven() {
        return dataTable != null;
    }

    public DataTable getDataTable() {
        return dataTable;
    }
}
