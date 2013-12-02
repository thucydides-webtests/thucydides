package net.thucydides.core.reports;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.requirements.RequirementsService;
import net.thucydides.core.util.EnvironmentVariables;

import static net.thucydides.core.ThucydidesSystemProperty.PROJECT_NAME;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_STEP_DETAILS;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_MANUAL_TESTS;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_RELEASES;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_PROGRESS;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_HISTORY;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_TAG_MENUS;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_RELATED_TAGS;
import static net.thucydides.core.ThucydidesSystemProperty.SHOW_PIE_CHARTS;

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
    final private boolean showRelatedTags;
    final private String projectName;

    private RequirementsService requirementsService;

    final private boolean displayPiechart;

    public ReportOptions(EnvironmentVariables environmentVariables) {
        showStepDetails = Boolean.valueOf(SHOW_STEP_DETAILS.from(environmentVariables, "false"));
        showManualTests = Boolean.valueOf(SHOW_MANUAL_TESTS.from(environmentVariables, "true"));
        showReleases = Boolean.valueOf(SHOW_RELEASES.from(environmentVariables, "true"));
        showProgress = Boolean.valueOf(SHOW_PROGRESS.from(environmentVariables, "false"));
        showHistory = Boolean.valueOf(SHOW_HISTORY.from(environmentVariables, "false"));
        showTagMenus = Boolean.valueOf(SHOW_TAG_MENUS.from(environmentVariables, "false"));
        showRelatedTags = Boolean.valueOf(SHOW_RELATED_TAGS.from(environmentVariables, "true"));
        displayPiechart = Boolean.valueOf(SHOW_PIE_CHARTS.from(environmentVariables, "true"));
        projectName = PROJECT_NAME.from(environmentVariables,"");
        requirementsService = Injectors.getInjector().getInstance(RequirementsService.class);
    }

    public boolean isShowStepDetails() {
        return showStepDetails;
    }

    public boolean isShowManualTests() {
        return showManualTests;
    }

    public boolean isShowReleases() {
        return showReleases || (!requirementsService.getReleasesFromRequirements().isEmpty());
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

    public boolean isShowRelatedTags() {
        return showRelatedTags;
    }

    public String getProjectName() {
        return projectName;
    }

    public boolean isDisplayPiechart() {
        return displayPiechart;
    }
}
