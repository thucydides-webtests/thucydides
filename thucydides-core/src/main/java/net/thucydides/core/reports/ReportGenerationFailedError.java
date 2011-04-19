package net.thucydides.core.reports;

/**
 * Report generation has failed for some reason.
 */
public class ReportGenerationFailedError extends RuntimeException {

    public ReportGenerationFailedError(final String message, final Throwable e) {
        super(message, e);
    }
}
