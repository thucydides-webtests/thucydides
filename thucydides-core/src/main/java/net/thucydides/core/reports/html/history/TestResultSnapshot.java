package net.thucydides.core.reports.html.history;

import org.joda.time.DateTime;

public class TestResultSnapshot {

    private final DateTime time;
    private final int specifiedSteps;
    private final int passingSteps;
    private final int failingSteps;
    private final int skippedSteps;

    public TestResultSnapshot(final int specifiedSteps,
                              final int passingSteps,
                              final int failingSteps,
                              final int skippedSteps) {
        this.time = DateTime.now();
        this.specifiedSteps = specifiedSteps;
        this.passingSteps = passingSteps;
        this.failingSteps = failingSteps;
        this.skippedSteps = skippedSteps;
    }

    public DateTime getTime() {
        return time;
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
}
