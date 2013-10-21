package net.thucydides.core.reports;

import net.thucydides.core.util.EnvironmentVariables;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_STEP_DETAILS;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_MANUAL_TESTS;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_RELEASES;

/**
 * Encapsulates user-specified formatting options for the generated reports.
 */
public class ReportOptions {

    final private boolean showStepDetails;
    final private boolean showManualTests;
    final private boolean showReleases;

    public ReportOptions(EnvironmentVariables environmentVariables) {
        showStepDetails = Boolean.valueOf(SHOW_STEP_DETAILS.from(environmentVariables, "false"));
        showManualTests = Boolean.valueOf(SHOW_MANUAL_TESTS.from(environmentVariables, "true"));
        showReleases = Boolean.valueOf(SHOW_RELEASES.from(environmentVariables, "true"));
    }

    public boolean isShowStepDetails() {
        return showStepDetails;
    }

    public boolean isShowManualTests() {
        return showManualTests;
    }

    public boolean isShowReleases() {
        return showReleases;
    }
}
