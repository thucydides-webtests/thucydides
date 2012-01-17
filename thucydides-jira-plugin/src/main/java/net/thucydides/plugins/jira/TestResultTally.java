package net.thucydides.plugins.jira;

import com.google.common.collect.Maps;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestResultList;

import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentMap;

public class TestResultTally {
    
    private final ConcurrentMap<String, List<TestResult>> testResultsTally;

    public TestResultTally() {
        this.testResultsTally = Maps.newConcurrentMap();
    }

    public synchronized void recordResult(String issueNumber, TestResult result) {
        getResultTallyForIssue(issueNumber).add(result);

    }
    
    protected List<TestResult> getResultTallyForIssue(final String issueNumber) {
        List<TestResult> resultTallyForIssue = testResultsTally.get(issueNumber);
        if (resultTallyForIssue == null) {
            testResultsTally.putIfAbsent(issueNumber, new Vector<TestResult>());
        }
        return testResultsTally.get(issueNumber);
    }
    
    public TestResult getResultForIssue(final String issueNumber) {
        TestResultList overallResults = new TestResultList(getResultTallyForIssue(issueNumber));
        return overallResults.getOverallResult();
    }

    public Set<String> getIssues() {
        return testResultsTally.keySet();
    }
}
