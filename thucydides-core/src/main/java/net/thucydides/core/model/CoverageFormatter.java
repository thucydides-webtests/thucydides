package net.thucydides.core.model;

public class CoverageFormatter {

    private final double passing;
    private final double pending;
    private final double failing;
    private final double error;

    private final NumericalFormatter formatter;

    public CoverageFormatter(double passing, double pending, double failing, double error) {
        this.passing = passing;
        this.pending = pending;
        this.failing = failing;
        this.error = error;
        formatter = new NumericalFormatter();
    }

    public String getPercentPassingCoverage() {
        return formatter.percentage(passing,1);
    }


    public String getPercentFailingCoverage() {
        return formatter.percentage(failing,1);
    }

    public String getPercentPendingCoverage() {
        return formatter.percentage(pending,1);
    }

    public String getPercentErrorCoverage() {
        return formatter.percentage(error,1);
    }
}

