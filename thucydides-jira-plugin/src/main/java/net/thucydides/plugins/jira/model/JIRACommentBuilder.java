package net.thucydides.plugins.jira.model;

import ch.lambdaj.function.convert.Converter;
import net.thucydides.core.model.TestOutcome;

import java.util.List;

import static ch.lambdaj.Lambda.convert;

public class JIRACommentBuilder {
    private final String testRunNumber;
    private final String reportUrl;
    private final List<NamedTestResult> namedTestResults;

    private final static String NEW_LINE = System.getProperty("line.separator");

    public JIRACommentBuilder() {
        this(null);
    }

    public JIRACommentBuilder(final String reportUrl) {
        this(reportUrl, null, null);
    }

    public JIRACommentBuilder(final String reportUrl,
                              final List<TestOutcome> testOutcomes) {
        this(testOutcomes, reportUrl, null);
    }


    public JIRACommentBuilder(final List<TestOutcome> testOutcomes,
                              final String reportUrl,
                              final String testRunNumber) {
        this(reportUrl, namedTestResultsFrom(testOutcomes), testRunNumber);
    }

    private static List<NamedTestResult> namedTestResultsFrom(List<TestOutcome> testOutcomes) {
        return convert(testOutcomes, toNamedTestResults());
    }

    private static Converter<TestOutcome, NamedTestResult> toNamedTestResults() {
        return new Converter<TestOutcome, NamedTestResult>() {

            public NamedTestResult convert(TestOutcome from) {
                return new NamedTestResult(from.getTitle(), from.getResult());
            }
        };
    }

    public JIRACommentBuilder(String reportUrl,
                              List<NamedTestResult> namedTestResults,
                              String testRunNumber) {
        this.reportUrl = reportUrl;
        this.namedTestResults = namedTestResults;
        this.testRunNumber = testRunNumber;
    }


    public String asText() {
        StringBuilder commentBuilder = new StringBuilder();
        addLine(commentBuilder, "Thucydides Test Results");
        addLine(commentBuilder, "Report: " + reportUrl);
        addLine(commentBuilder, "Test Run: " + testRunNumber);
        addLineForEachTest(commentBuilder);
        return commentBuilder.toString();
    }

    private void addLineForEachTest(StringBuilder commentBuilder) {
        if (namedTestResults != null) {
            for (NamedTestResult testResult : namedTestResults) {
                addLine(commentBuilder, testResult.getTestName() + ": "  + testResult.getTestResult());
            }
        }
    }

    private void addLine(StringBuilder commentBuilder, final String line) {
        commentBuilder.append(line).append(NEW_LINE);
    }

    public JIRACommentBuilder withResults(final List<TestOutcome> testOutcomes) {
        return new JIRACommentBuilder(reportUrl, testOutcomes);
    }

    public JIRACommentBuilder withTestRun(final String testRunNumber) {
        return new JIRACommentBuilder(this.reportUrl, this.namedTestResults, testRunNumber);
    }

    public JIRACommentBuilder withReportUrl(final String reportUrl) {
        return new JIRACommentBuilder(reportUrl, this.namedTestResults, this.testRunNumber);
    }

    public JIRACommentBuilder withNamedResults(List<NamedTestResult> namedTestResults) {
        return new JIRACommentBuilder(this.reportUrl, namedTestResults, this.testRunNumber);
    }

    public TestResultComment asComment() {
        return new TestResultComment(reportUrl, testRunNumber, namedTestResults);
    }
}
