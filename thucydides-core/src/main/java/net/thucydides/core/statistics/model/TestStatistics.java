package net.thucydides.core.statistics.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class TestStatistics {

    private final Long totalTestRuns;
    private final Long passingTestRuns;
    private final List<TestRunTag> tags;

    public TestStatistics(Long totalTestRuns,
                          Long passingTestRuns,
                          List<TestRunTag> tags) {
        this.totalTestRuns = totalTestRuns;
        this.passingTestRuns = passingTestRuns;
        this.tags = ImmutableList.copyOf(tags);
    }

    public Long getTotalTestRuns() {
        return totalTestRuns;
    }

    public Long getPassingTestRuns() {
        return passingTestRuns;
    }

    public Double getPassRate() {
        if (totalTestRuns > 0) {
            return (double) passingTestRuns / (double) totalTestRuns;
        } else {
            return 0.0;
        }
    }

    public List<TestRunTag> getTags() {
        return tags;
    }
}
