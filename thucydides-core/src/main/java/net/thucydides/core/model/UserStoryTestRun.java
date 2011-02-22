package net.thucydides.core.model;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of test runs, corresponding to the acceptance tests for a given user story.
 * @author johnsmart
 *
 */
public class UserStoryTestRun {
    
    private final List<AcceptanceTestRun> testRuns = new ArrayList<AcceptanceTestRun>();

    public void addAcceptanceTestRun(final AcceptanceTestRun testRun) {
        testRuns.add(testRun);
    }

    public int getTestRunCount() {
        return testRuns.size();
    }

    public int getSuccessfulTestRunCount() {
        return select(testRuns, having(on(AcceptanceTestRun.class).isSuccess())).size();
    }

    public Integer getFailedTestRunCount() {
        return select(testRuns, having(on(AcceptanceTestRun.class).isFailure())).size();
    }

}
