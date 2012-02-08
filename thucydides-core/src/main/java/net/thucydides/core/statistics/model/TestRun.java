package net.thucydides.core.statistics.model;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Immutable
public class TestRun {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private TestResult result;
    private Date executionDate;
    private long duration;

    public TestRun() {}

    protected TestRun(String title, TestResult result, long duration, Date executionDate) {
        this.title = title;
        this.result = result;
        this.executionDate = executionDate;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public TestResult getResult() {
        return result;
    }

    public Date getExecutionDate() {
        return new Date(executionDate.getTime());
    }

    public long getDuration() {
        return duration;
    }

    public static TestRun from(final TestOutcome result) {
        return new TestRun(result.getTitle(), result.getResult(), result.getDuration(), null);
    }

    public TestRun at(final Date executionDate) {
        return new TestRun(getTitle(), getResult(), getDuration(), executionDate);
    }
}
