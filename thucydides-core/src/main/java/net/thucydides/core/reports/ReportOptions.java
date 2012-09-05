package net.thucydides.core.reports;

import net.thucydides.core.util.EnvironmentVariables;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_STEP_DETAILS;

/**
 * Encapsulates user-specified formatting options for the generated reports.
 */
public class ReportOptions {

    final private boolean showStepDetails;

    public ReportOptions(EnvironmentVariables environmentVariables) {
        showStepDetails = Boolean.valueOf(SHOW_STEP_DETAILS.from(environmentVariables, "false"));
    }

    public boolean isShowStepDetails() {
        return showStepDetails;
    }
}
