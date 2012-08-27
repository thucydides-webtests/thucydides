package net.thucydides.core.reports.html.history;

import net.thucydides.core.Thucydides;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.jdbc.Strategy;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class TestResultSnapshot implements Comparable<TestResultSnapshot> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_result_snapshot_seq")
    @SequenceGenerator(name="test_result_snapshot_seq",sequenceName="SNAPSHOT_SEQUENCE", allocationSize=1)
    private Long id;

    @Transient
    private DateTime time;
    private Timestamp timestamp;

    private int specifiedSteps;
    private int passingSteps;
    private int failingSteps;
    private int skippedSteps;
    private String buildId;
    private String projectKey;




    public TestResultSnapshot() {}

    public TestResultSnapshot(final DateTime time,
                              final int specifiedSteps,
                              final int passingSteps,
                              final int failingSteps,
                              final int skippedSteps,
                              final String buildId) {

        this(time,specifiedSteps,passingSteps,failingSteps,skippedSteps,buildId, Thucydides.getDefaultProjectKey());
    }

    public TestResultSnapshot(final int specifiedSteps,
                              final int passingSteps,
                              final int failingSteps,
                              final int skippedSteps,
                              final String buildId) {
        this(DateTime.now(),specifiedSteps,passingSteps,failingSteps,skippedSteps,buildId);
    }

    public TestResultSnapshot(final int specifiedSteps,
                              final int passingSteps,
                              final int failingSteps,
                              final int skippedSteps,
                              final String buildId,
                              final String projectKey) {
        this(DateTime.now(),specifiedSteps,passingSteps,failingSteps,skippedSteps,buildId, projectKey);

    }

    public TestResultSnapshot(final DateTime time,
                              final int specifiedSteps,
                              final int passingSteps,
                              final int failingSteps,
                              final int skippedSteps,
                              final String buildId,
                              final String projectKey) {
        this.time = time;
        this.timestamp = new Timestamp(time.getMillis());
        this.specifiedSteps = specifiedSteps;
        this.passingSteps = passingSteps;
        this.failingSteps = failingSteps;
        this.skippedSteps = skippedSteps;
        this.buildId = buildId;
        this.projectKey = projectKey;
    }

    public DateTime getTime() {
        return new DateTime(timestamp);
    }

    public Timestamp getTimeStamp() {
        return timestamp;
    }

    public int getSpecifiedSteps() {
        return specifiedSteps;
    }

    public int getPassingSteps() {
        return passingSteps;
    }

    public int getFailingSteps() {
        return failingSteps;
    }

    public int getSkippedSteps() {
        return skippedSteps;
    }

    public String getBuildId() {
        return buildId;
    }

    public int compareTo(TestResultSnapshot other) {
        if (this == other) {
            return 0;
        } else {
            return this.getTime().compareTo(other.getTime());
        }
    }


    @Override
    public String toString() {
        return "TestResultSnapshot{" +
                "id=" + id +
                ", time=" + time +
                ", specifiedSteps=" + specifiedSteps +
                ", passingSteps=" + passingSteps +
                ", failingSteps=" + failingSteps +
                ", skippedSteps=" + skippedSteps +
                ", buildId='" + buildId + '\'' +
                ", projectKey='" + projectKey + '\'' +
                '}';
    }

}
