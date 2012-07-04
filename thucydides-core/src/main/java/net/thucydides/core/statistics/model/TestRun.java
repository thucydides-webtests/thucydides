package net.thucydides.core.statistics.model;

import com.google.common.collect.ImmutableSet;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import org.eclipse.persistence.annotations.ReadOnly;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Immutable
//@ReadOnly
public class TestRun {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @SequenceGenerator(name="seq",sequenceName="HIBERNATE_SEQUENCE", allocationSize=1)
    private Long id;

    private String title;
    private String projectKey;
    private TestResult result;

    @Temporal(TemporalType.TIMESTAMP)
    private Date executionDate;
    private long duration;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "testrun_tags",
            joinColumns = {@JoinColumn(name = "testrun_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private Set<TestRunTag> tags = new HashSet<TestRunTag>();

    public TestRun() {}

    protected TestRun(String title, String projectKey, TestResult result, long duration, Date executionDate) {
        this.title = title;
        this.projectKey = projectKey;
        this.result = result;
        this.executionDate = executionDate;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public TestResult getResult() {
        return result;
    }


    public Date getExecutionDate() {
        return (executionDate == null) ? null : new Date(executionDate.getTime());
    }

    public long getDuration() {
        return duration;
    }

    public Set<TestRunTag> getTags() {
        return tags;
    }

    public static TestRun from(final TestOutcome result) {
        return new TestRun(result.getTitle(), null, result.getResult(), result.getDuration(), null);
    }

    public TestRun inProject(final String projectKey) {
        return new TestRun(getTitle(), projectKey, getResult(), getDuration(), getExecutionDate());
    }

    public TestRun at(final Date executionDate) {
        return new TestRun(getTitle(), getProjectKey(), getResult(), getDuration(), executionDate);
    }

    @Override
    public String toString() {
        return "TestRun{" +
                "title='" + title + '\'' +
                ", projectKey='" + projectKey + '\'' +
                '}';
    }
}
