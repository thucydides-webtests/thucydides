package net.thucydides.core.reports;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.CoverageFormatter;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestResultList;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.webdriver.Configuration;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.sort;
import static ch.lambdaj.Lambda.sum;
import static net.thucydides.core.reports.matchers.TestOutcomeMatchers.havingTagName;
import static net.thucydides.core.reports.matchers.TestOutcomeMatchers.havingTagType;
import static net.thucydides.core.reports.matchers.TestOutcomeMatchers.withResult;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;

/**
 * A set of test outcomes, which lets you perform query operations on the test outcomes.
 * In particular, you can filter a set of test outcomes by tag type and by tag values.
 * Since these operations also return TestOucomes, you can then further drill down into the test
 * outcome sets.
 */
public class TestOutcomes {

    private final List<TestOutcome> outcomes;
    private final double estimatedAverageStepCount;

    /**
     * A label indicating where these tests come from (e.g. the tag, the result status, etc).
     */
    private final String label;

    @Inject
    protected TestOutcomes(List<TestOutcome> outcomes,
                          double estimatedAverageStepCount,
                          String label) {
        this.outcomes = ImmutableList.copyOf(outcomes);
        this.estimatedAverageStepCount = estimatedAverageStepCount;
        this.label = label;
    }

    protected TestOutcomes(List<TestOutcome> outcomes,
                           double estimatedAverageStepCount) {
        this(outcomes, estimatedAverageStepCount, "");
    }

    protected TestOutcomes withLabel(String label) {
        return new TestOutcomes(this.outcomes, this.estimatedAverageStepCount, label);
    }
    
    public static TestOutcomes of(List<TestOutcome> outcomes) {
        return new TestOutcomes(outcomes, 
                                Injectors.getInjector().getInstance(Configuration.class)
                                                       .getEstimatedAverageStepCount());
    }

    public String getLabel() {
        return label;
    }

    /**
     * @return The list of all of the different tag types that appear in the test outcomes.
     */
    public List<String> getTagTypes() {
        Set<String> tagTypes = Sets.newHashSet();
        for(TestOutcome outcome : outcomes) {
            tagTypes.addAll(extract(outcome.getTags(), on(TestTag.class).getType()));
        }
        return sort(ImmutableList.copyOf(tagTypes), on(String.class));
    }

    /**
     * @return The list of all the different tags in these test outcomes
     */
    public List<String> getTags() {
        Set<String> tags = Sets.newHashSet();
        for(TestOutcome outcome : outcomes) {
            tags.addAll(extract(outcome.getTags(), on(TestTag.class).getName()));
        }
        return sort(ImmutableList.copyOf(tags), on(String.class));
    }

    /**
     * @return The list of all the tags associated with a given tag type.
     */
    public List<String> getTagsOfType(String tagType) {
        Set<String> tags = Sets.newHashSet();
        for(TestOutcome outcome : outcomes) {
            tags.addAll(tagsOfType(tagType).in(outcome));
        }
        return sort(ImmutableList.copyOf(tags), on(String.class));
    }

    private TagFinder tagsOfType(String tagType) {
        return new TagFinder(tagType);
    }
    
    private class TagFinder {
        private final String tagType;

        private TagFinder(String tagType) {
            this.tagType = tagType;
        }
        
        List<String> in(TestOutcome testOutcome) {
            List<String> matchingTags = Lists.newArrayList();
            for(TestTag tag : testOutcome.getTags()) {
                if (tag.getType().equals(tagType)) {
                    matchingTags.add(tag.getName());
                }
            }
            return ImmutableList.copyOf(matchingTags);
        }
    }

    /**
     * Find the test outcomes with a given tag type
     * @param tagType the tag type we are filtering on
     * @return A new set of test outcomes for this tag type
     */
    public TestOutcomes withTagType(String tagType) {
        return TestOutcomes.of(filter(havingTagType(tagType), outcomes)).withLabel(tagType);
    }

    /**
     * Find the test outcomes with a given tag name
     * @param tagName the name of the tag type we are filtering on
     * @return A new set of test outcomes for this tag name
     */
    public TestOutcomes withTag(String tagName) {
        return TestOutcomes.of(filter(havingTagName(tagName), outcomes)).withLabel(tagName);
    }

    /**
     * Find the failing test outcomes in this set
     * @return A new set of test outcomes containing only the failing tests
     */
    public TestOutcomes getFailingTests() {
        return TestOutcomes.of(filter(withResult(TestResult.FAILURE), outcomes))
                            .withLabel(labelForTestsWithStatus("failing tests"));
    }

    private String labelForTestsWithStatus(String status) {
        if (StringUtils.isEmpty(label)) {
            return status;
        } else {
            return label + " (" + status + ")";
        }
    }

    /**
     * Find the successful test outcomes in this set
     * @return A new set of test outcomes containing only the successful tests
     */
    public TestOutcomes getPassingTests() {
        return TestOutcomes.of(filter(withResult(TestResult.SUCCESS), outcomes))
                            .withLabel(labelForTestsWithStatus("passing tests"));
    }

    /**
     * Find the pending or ignored test outcomes in this set
     * @return A new set of test outcomes containing only the pending or ignored tests
     */
    public TestOutcomes getPendingTests() {
        return TestOutcomes.of(filter(anyOf(withResult(TestResult.PENDING), withResult(TestResult.SKIPPED)), outcomes))
                           .withLabel(labelForTestsWithStatus("pending tests"));

    }

    /**
     * @return The list of TestOutcomes contained in this test outcome set.
     */
    public List<TestOutcome> getTests() {
        return sort(outcomes, on(TestOutcome.class).getTitle());
    }

    /**
     * @return The total duration of all of the tests in this set in milliseconds.
     */
    public long getDuration() {
        return sum(outcomes, on(TestOutcome.class).getDuration());
    }

    /**
     * @return The total number of test runs in this set.
     *
     */
    public int getTotal() {
        return outcomes.size();
    }

    /**
     * @return The overall result for the tests in this test outcome set.
     */
    public TestResult getResult() {
        TestResultList testResults = TestResultList.of(getCurrentTestResults());
        return testResults.getOverallResult();
    }

    private List<TestResult> getCurrentTestResults() {
        return convert(outcomes, toTestResults());
    }

    private Converter<TestOutcome, TestResult> toTestResults() {
        return new Converter<TestOutcome, TestResult>() {
            public TestResult convert(final TestOutcome step) {
                return step.getResult();
            }
        };
    }

    /**
     * @return The total number of nested steps in these test outcomes.
     */
    public int getStepCount() {
        return sum(extract(outcomes, on(TestOutcome.class).getNestedStepCount())).intValue();
    }

    /**
     * @return The number of successful tests in this set.
     */
    public int getSuccessCount() {
        return select(outcomes, having(on(TestOutcome.class).isSuccess())).size();
    }

    /**
     * @return How many test cases contain at least one failing test.
     */
    public int getFailureCount() {
        return select(outcomes, having(on(TestOutcome.class).isFailure())).size();
    }

    /**
     * @return How many test cases contain at least one pending test.
     */
    public int getPendingCount() {
        return select(outcomes, having(on(TestOutcome.class).isPending())).size();
    }

    /**
     * @return How many tests have been skipped.
     */
    public int getSkipCount() {
        return select(outcomes, having(on(TestOutcome.class).isSkipped())).size();
    }

    /**
     * @return The percent of passing steps, based on the real and estimated test size in terms of the relative number
     * of steps.
     */
    public Double getPercentagePassingStepCount() {
        return (countStepsWithResultThat(is(TestResult.SUCCESS)) / (double) getEstimatedTotalStepCount());
    }

    /**
     * @return The percent of failing steps, based on the real and estimated test size in terms of the relative number
     * of steps.
     */
    public Double getPercentageFailingStepCount() {
        return (countStepsWithResultThat(is(TestResult.FAILURE)) / (double) getEstimatedTotalStepCount());
    }

    /**
     * @return The percent of pending steps, based on the real and estimated test size in terms of the relative number
     * of steps.
     */
    public Double getPercentagePendingStepCount() {
        int passingOrFailingSteps = countStepsWithResultThat(isOneOf(TestResult.SUCCESS, TestResult.FAILURE));
        return ((getEstimatedTotalStepCount() - passingOrFailingSteps) / (double) getEstimatedTotalStepCount());
    }

    /**
     *
     * @return Formatted version of the test coverage metrics
     */
    public CoverageFormatter getFormatted() {
        return new CoverageFormatter(getPercentagePassingStepCount(),
                                     getPercentagePendingStepCount(),
                                     getPercentageFailingStepCount());
    }

    private int countStepsWithResultThat(Matcher<TestResult> matchingResult) {
        List<TestOutcome> matchingTests = select(outcomes, having(on(TestOutcome.class).getResult(), matchingResult));
        return (matchingTests.isEmpty()) ? 0 : sum(matchingTests, on(TestOutcome.class).getNestedStepCount());
    }

    private Integer getEstimatedTotalStepCount() {
        return (getStepCount() + estimatedUnimplementedStepCount());
    }

    private Integer estimatedUnimplementedStepCount() {
        return (int) (Math.round(getAverageTestSize() * totalUnimplementedTests()));
    }

    public double getAverageTestSize() {
        if (totalImplementedTests() > 0) {
            return ((double) getStepCount()) / totalImplementedTests();
        } else {
            return estimatedAverageStepCount;
        }
    }

    private int totalUnimplementedTests() {
        return getTotal() - totalImplementedTests();
    }

    private int totalImplementedTests() {
        return filter(having(on(TestOutcome.class).getTestSteps().isEmpty(), is(false)), outcomes).size();
    }

}
