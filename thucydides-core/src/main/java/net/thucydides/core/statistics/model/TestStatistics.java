package net.thucydides.core.statistics.model;

public class TestStatistics {

    private final Long totalTestRuns;
    private final Long passingTestRuns;

    public TestStatistics(Long totalTestRuns,
                          Long passingTestRuns) {
        this.totalTestRuns = totalTestRuns;
        this.passingTestRuns = passingTestRuns;
    }

    public Long getTotalTestRuns() {
        return totalTestRuns;
    }

    public Long getPassingTestRuns() {
        return passingTestRuns;
    }
}
