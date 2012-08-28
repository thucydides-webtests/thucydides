package net.thucydides.core.reports.history;

import org.joda.time.DateTime;

public class ProgressSnapshot implements Comparable<ProgressSnapshot> {

    private final DateTime time;
    private final String requirementType;
    private final int total;
    private final int passing;
    private final String buildId;

    public ProgressSnapshot(DateTime time, String requirementType, int total, int passing, String buildId) {
        this.time = time;
        this.requirementType = requirementType;
        this.total = total;
        this.passing = passing;
        this.buildId = buildId;
    }

    public ProgressSnapshot(String requirementType, int total, int passing, String buildId) {
        this(DateTime.now(), requirementType, total, passing, buildId);
    }

    public DateTime getTime() {
        return time;
    }

    public String getRequirementType() {
        return requirementType;
    }

    public int getTotal() {
        return total;
    }

    public int getPassing() {
        return passing;
    }

    public String getBuildId() {
        return buildId;
    }

    public int compareTo(ProgressSnapshot other) {
        if (this == other) {
            return 0;
        } else {
            return this.getTime().compareTo(other.getTime());
        }
    }
}
