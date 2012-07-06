package net.thucydides.core.statistics.model;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Immutable
public class TestRunTag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @SequenceGenerator(name="seq",sequenceName="HIBERNATE_SEQUENCE", allocationSize=1)
    private Long id;

    private String projectKey;
    private String name;
    private String type;

    public TestRunTag() {
    }

    public TestRunTag(String projectKey, String type, String name) {
        this.projectKey = projectKey;
        this.type = type;
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.PERSIST)
    private Set<TestRun> testRuns = new HashSet<TestRun>();

    public Set<TestRun> getTestRuns() {
        return testRuns;
    }

    @Override
    public String toString() {
        return "TestRunTag{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", projectKey='" + projectKey + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
