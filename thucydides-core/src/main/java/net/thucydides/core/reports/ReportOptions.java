package net.thucydides.core.reports;

import net.thucydides.core.util.EnvironmentVariables;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_STEP_DETAILS;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_MANUAL_TESTS;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_RELEASES;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_PROGRESS;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_HISTORY;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_TAG_MENUS;

/**
 * Encapsulates user-specified formatting options for the generated reports.
 */
public class ReportOptions {

    final private boolean showStepDetails;
    final private boolean showManualTests;
    final private boolean showReleases;
    final private boolean showProgress;
    final private boolean showHistory;
    final private boolean showTagMenus;

    public ReportOptions(EnvironmentVariables environmentVariables) {
        showStepDetails = Boolean.valueOf(SHOW_STEP_DETAILS.from(environmentVariables, "false"));
        showManualTests = Boolean.valueOf(SHOW_MANUAL_TESTS.from(environmentVariables, "true"));
        showReleases = Boolean.valueOf(SHOW_RELEASES.from(environmentVariables, "true"));
        showProgress = Boolean.valueOf(SHOW_PROGRESS.from(environmentVariables, "false"));
        showHistory = Boolean.valueOf(SHOW_HISTORY.from(environmentVariables, "false"));
        showTagMenus = Boolean.valueOf(SHOW_TAG_MENUS.from(environmentVariables, "false"));
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

    public boolean isShowProgress() {
        return showProgress;
    }

    public boolean isShowHistory() {
        return showHistory;
    }

    public boolean isShowTagMenus() {
        return showTagMenus;
    }
}
