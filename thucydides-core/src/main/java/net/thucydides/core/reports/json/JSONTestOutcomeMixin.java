package net.thucydides.core.reports.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.statistics.model.TestStatistics;
import net.thucydides.core.util.EnvironmentVariables;
import org.joda.time.DateTime;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static net.thucydides.core.model.TestResult.ERROR;
import static net.thucydides.core.model.TestResult.FAILURE;
import static net.thucydides.core.model.TestResult.IGNORED;
import static net.thucydides.core.model.TestResult.PENDING;
import static net.thucydides.core.model.TestResult.SKIPPED;
import static net.thucydides.core.model.TestResult.SUCCESS;


@JsonIgnoreProperties({"testResult", "htmlReport", "reportName", "screenshotReportName", "descriptionText",
        "screenshots", "screenshotAndHtmlSources","qualifiedMethodName",
        "environmentVariables", "overallStability", "statistics",
        "recentStability", "recentTestRunCount", "recentPassCount", "recentFailCount", "recentPendingCount",
        "dataDriven", "stepCount", "nestedStepCount", "successCount", "failureCount", "errorCount",
        "ignoredCount", "skippedOrIgnoredCount", "skippedCount", "pendingCount",
        "titleWithLinks", "testCount", "startTimeNotDefined",
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
