package net.thucydides.core.reports.json.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@JsonIgnoreProperties({"testResult", "htmlReport", "reportName", "screenshotReportName", "descriptionText",
        "screenshots", "screenshotAndHtmlSources","qualifiedMethodName",
        "environmentVariables", "overallStability", "statistics",
        "recentStability", "recentTestRunCount", "recentPassCount", "recentFailCount", "recentPendingCount",
        "dataDriven", "stepCount", "nestedStepCount", "successCount", "failureCount", "errorCount",
        "ignoredCount", "skippedOrIgnoredCount", "skippedCount", "pendingCount",
        "titleWithLinks", "testCount", "startTimeNotDefined", "completeName",
        "flattenedTestSteps", "leafTestSteps", "formattedIssues", "issueKeys",
        "success","error","failure","pending","skipped",
        "path","pathId","storyTitle",
        "durationInSeconds", "videoLinks", "implementedTestCount", "exampleFields", "dataDrivenSampleScenario"})
@JsonInclude(NON_NULL)
public abstract class JSONTestOutcomeMixin {
    public JSONTestOutcomeMixin(@JsonProperty("name") String methodName) {
    }

    @JsonProperty("name")
    public abstract String getMethodName();

    @JsonProperty("stepCount")
    public abstract Integer getStepCount();

    @JsonProperty("successCount")
    public abstract Integer getSuccessCount();

    @JsonProperty("failureCount")
    public abstract Integer getFailureCount();

    @JsonProperty("errorCount")
    public abstract Integer getErrorCount();

    @JsonProperty("ignoredCount")
    public abstract Integer getIgnoredCount();

    @JsonProperty("skippedOrIgnoredCount")
    public abstract Integer getSkippedOrIgnoredCount();

    @JsonProperty("skippedCount")
    public abstract Integer getSkippedCount();

    @JsonProperty("pendingCount")
    public abstract Integer getPendingCount();

    @JsonProperty("result")
    public abstract TestResult getResult();

    @JsonProperty("testSteps")
    public abstract List<TestStep> getTestSteps();
}
