package net.thucydides.plugins.jira;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestResultList;

import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentMap;

import static ch.lambdaj.Lambda.convert;

public class TestResultTally {
    
    private final ConcurrentMap<String, List<TestOutcome>> testOutcomesTally;

    public TestResultTally() {
        this.testOutcomesTally = Maps.newConcurrentMap();
    }

    public synchronized void recordResult(String issueNumber, TestOutcome outcome) {
        getTestOutcomeListForIssue(issueNumber).add(outcome);

    }

    public List<TestOutcome> getTestOutcomesForIssue(String issueNumber) {
       return ImmutableList.copyOf(getTestOutcomeListForIssue(issueNumber));

    }

    protected List<TestOutcome> getTestOutcomeListForIssue(final String issueNumber) {
        List<TestOutcome> resultTallyForIssue = testOutcomesTally.get(issueNumber);
        if (resultTallyForIssue == null) {
            testOutcomesTally.putIfAbsent(issueNumber, new Vector<TestOutcome>());
        }
        return testOutcomesTally.get(issueNumber);
    }
    
    public TestResult getResultForIssue(final String issueNumber) {
        List<TestOutcome> testOutcomesForThisIssue = testOutcomesTally.get(issueNumber);
        TestResultList overallResults = TestResultList.of(convert(testOutcomesForThisIssue, toTestResults()));
        return overallResults.getOverallResult();
    }

    private Converter<TestOutcome, TestResult> toTestResults() {
        return new Converter<TestOutcome, TestResult>() {
            public TestResult convert(TestOutcome from) {
                return from.getResult();
            }
        };
    }

    public Set<String> getIssues() {
        return testOutcomesTally.keySet();
    }
}
