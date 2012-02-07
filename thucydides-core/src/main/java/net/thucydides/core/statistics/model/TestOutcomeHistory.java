package net.thucydides.core.statistics.model;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import org.joda.time.DateTime;

/**
 * A description goes here.
 * User: johnsmart
 * Date: 7/02/12
 * Time: 3:57 PM
 */
public class TestOutcomeHistory {
    
    private final String title;
    private final TestResult result;
    private final DateTime executionDate;

    protected TestOutcomeHistory(String title, TestResult result, DateTime executionDate) {
        this.title = title;
        this.result = result;
        this.executionDate = executionDate;
    }

    public String getTitle() {
        return title;
    }

    public TestResult getResult() {
        return result;
    }

    public DateTime getExecutionDate() {
        return executionDate;
    }

    public static TestOutcomeHistory from(final TestOutcome result) {
        return new TestOutcomeHistory(result.getTitle(), result.getResult(), null);
    }
    
    public TestOutcomeHistory at(final DateTime executionDate) {
        return new TestOutcomeHistory(getTitle(), getResult(), executionDate);
    }
}
