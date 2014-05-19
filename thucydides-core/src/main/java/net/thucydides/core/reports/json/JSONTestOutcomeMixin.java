package net.thucydides.core.reports.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@JsonIgnoreProperties({"testResult", "htmlReport", "reportName", "screenshotReportName", "descriptionText",
        "screenshots", "screenshotAndHtmlSources","qualifiedMethodName",
        "environmentVariables", "overallStability", "statistics",
        "recentStability", "recentTestRunCount", "recentPassCount", "recentFailCount", "recentPendingCount",
        "dataDriven", "stepCount", "nestedStepCount", "successCount", "failureCount", "errorCount",
        "ignoredCount", "skippedOrIgnoredCount", "skippedCount", "pendingCount",
        "titleWithLinks", "testCount", "startTimeNotDefined", "completeName",
        "flattenedTestSteps", "leafTestSteps", "formattedIssues", "issueKeys",
        "durationInSeconds", "videoLinks", "implementedTestCount", "exampleFields", "dataDrivenSampleScenario"})
@JsonInclude(NON_NULL)
public abstract class JSONTestOutcomeMixin {
    public JSONTestOutcomeMixin(@JsonProperty("name") String methodName) {
    }

    @JsonProperty("name")
    public abstract String getMethodName();

    @JsonProperty("steps")
    public abstract Integer getStepCount();

    @JsonProperty("success")
    public abstract Integer getSuccessCount();

    @JsonProperty("failure")
    public abstract Integer getFailureCount();

    @JsonProperty("error")
    public abstract Integer getErrorCount();

    @JsonProperty("ignored")
    public abstract Integer getIgnoredCount();

    @JsonProperty("skippedOrIgnored")
    public abstract Integer getSkippedOrIgnoredCount();

    @JsonProperty("skipped")
    public abstract Integer getSkippedCount();

    @JsonProperty("pending")
    public abstract Integer getPendingCount();
}
