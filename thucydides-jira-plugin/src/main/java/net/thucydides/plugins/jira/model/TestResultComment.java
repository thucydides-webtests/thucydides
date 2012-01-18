package net.thucydides.plugins.jira.model;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestResultList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;

public class TestResultComment {

    private final String testRunNumber;
    private final SortedMap<String, NamedTestResult> namedTestResults;
    private final String reportUrl;

    private final static int REPORT_URL_LINE = 1;
    private final static int TEXT_NUMBER_LINE = 2;
    private final static int FIRST_TEST_RESULT_LINE = 3;

    protected TestResultComment(String commentText) {
        List<String> commentLines = ImmutableList.copyOf(commentText.split("\\r?\\n"));
        reportUrl = findReportUrl(commentLines);
        testRunNumber = findTestRunNumber(commentLines);
        namedTestResults = findTestResults(commentLines);
    }

    protected TestResultComment(String reportUrl, String testRunNumber, List<NamedTestResult> namedTestResults) {
        this.reportUrl = reportUrl;
        this.testRunNumber = testRunNumber;
        this.namedTestResults = indexByTestName(namedTestResults);
    }

    public static JIRACommentBuilder comment() {
        return new JIRACommentBuilder();
    }

    public static TestResultComment fromText(String commentText) {
        return new TestResultComment(commentText);
    }

    private SortedMap<String, NamedTestResult> findTestResults(List<String> commentLines) {
        List<String> testResultLines = linesStartingAtRowIn(commentLines, FIRST_TEST_RESULT_LINE);

        List<NamedTestResult> namedTestResults = convert(testResultLines, toNamedTestResults());

        return indexByTestName(namedTestResults);
    }

    private SortedMap<String, NamedTestResult> indexByTestName(List<NamedTestResult> namedTestResults) {
        Map<String, NamedTestResult> indexedTestResults = index(namedTestResults, on(NamedTestResult.class).getTestName());
        SortedMap<String, NamedTestResult> sortedTestResults = Maps.newTreeMap();
        sortedTestResults.putAll(indexedTestResults);
        return sortedTestResults;
    }

    private List<String> linesStartingAtRowIn(List<String> commentLines, int startingIndex) {
        if (commentLines.size() >= startingIndex) {
            return commentLines.subList(startingIndex, commentLines.size());
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private Converter<String, NamedTestResult> toNamedTestResults() {
        return new Converter<String, NamedTestResult>() {

            public NamedTestResult convert(String commentLine) {
                String testName = textBeforeColon(commentLine);
                TestResult result = TestResult.valueOf(textAfterColon(commentLine));
                return new NamedTestResult(testName, result);
            }
        };
    }

    private String findTestRunNumber(List<String> commentLines) {
        if (commentLines.size() > TEXT_NUMBER_LINE) {
            return textAfterColon(commentLines.get(TEXT_NUMBER_LINE));
        } else {
            return null;
        }
    }

    private String findReportUrl(List<String> commentLines) {
        if (commentLines.size() > REPORT_URL_LINE) {
            return textAfterColon(commentLines.get(REPORT_URL_LINE));
        } else {
            return null;
        }
    }

    public String getReportUrl() {
        return reportUrl;
    }

    private String textBeforeColon(String line) {
        String[] lineTokens = splitAtColon(line);
        return lineTokens[0].trim();
    }

    private String[] splitAtColon(String line) {
        return line.split(":", 2);
    }

    private String textAfterColon(String line) {
        String[] lineTokens = splitAtColon(line);
        if (lineTokens.length >= 2) {
            return splitAtColon(line)[1].trim();
        } else {
            return null;
        }
    }

    public String getTestRunNumber() {
        return testRunNumber;
    }

    public List<NamedTestResult> getNamedTestResults() {
        return convert(namedTestResults.entrySet(), fromMapEntriesToNamedTestResults());
    }

    private Converter<Map.Entry<String, NamedTestResult>, NamedTestResult> fromMapEntriesToNamedTestResults() {
        return new Converter<Map.Entry<String, NamedTestResult>, NamedTestResult>() {
            public NamedTestResult convert(Map.Entry<String, NamedTestResult> from) {
                return from.getValue();
            }
        };
    }

    public TestResult getOverallResult() {
        List<TestResult> testResults = convert(namedTestResults, toTestResults());
        return TestResultList.of(testResults).getOverallResult();
    }

    private Converter<NamedTestResult, TestResult> toTestResults() {
        return new Converter<NamedTestResult, TestResult>() {

            public TestResult convert(NamedTestResult namedTestResult) {
                return namedTestResult.getTestResult();
            }
        };
    }

    public String asText() {
        return toString();
    }

    public String toString() {
        return comment().withTestRun(testRunNumber)
                .withReportUrl(reportUrl)
                .withNamedResults(getNamedTestResults())
                .asText();
    }

    public TestResultComment withUpdatedTestResults(final List<TestOutcome> newResults) {
        Map<String, NamedTestResult> mergedTestResultsIndexedByName = Maps.newHashMap();
        mergedTestResultsIndexedByName.putAll(namedTestResults);

        List<NamedTestResult> newTestResults = convert(newResults, fromTestOutcomesToNamedTestResults());
        for (NamedTestResult testResult : newTestResults) {
            mergedTestResultsIndexedByName.put(testResult.getTestName(), testResult);
        }

        List<NamedTestResult> mergedTestResults = Lists.newArrayList();
        mergedTestResults.addAll(mergedTestResultsIndexedByName.values());

        return comment().withTestRun(testRunNumber)
                .withReportUrl(reportUrl)
                .withNamedResults(mergedTestResults).asComment();
    }

    private Converter<TestOutcome, NamedTestResult> fromTestOutcomesToNamedTestResults() {
        return new Converter<TestOutcome, NamedTestResult>() {
            public NamedTestResult convert(TestOutcome testOutcome) {
                return new NamedTestResult(testOutcome.getTitle(), testOutcome.getResult());
            }
        };
    }

    public TestResultComment withUpdatedReportUrl(String newReportUrl) {
        return new TestResultComment(newReportUrl, this.testRunNumber, getNamedTestResults());
    }

    public TestResultComment withUpdatedTestRunNumber(String newTestRunNumber) {
        return new TestResultComment(this.reportUrl, newTestRunNumber, getNamedTestResults());
    }

}
