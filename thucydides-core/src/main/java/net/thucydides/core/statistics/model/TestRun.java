package net.thucydides.core.statistics.model;

import com.google.common.collect.ImmutableSet;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
            name = "testrun_tags",
            joinColumns = {@JoinColumn(name = "testrun_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private Set<TestRunTag> tags = new HashSet<TestRunTag>();

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

    public Set<TestRunTag> getTags() {
        return tags;
    }

    public static TestRun from(final TestOutcome result) {
        return new TestRun(result.getTitle(), result.getResult(), result.getDuration(), null);
    }

    public TestRun at(final Date executionDate) {
        return new TestRun(getTitle(), getResult(), getDuration(), executionDate);
    }
}
