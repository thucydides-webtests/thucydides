package net.thucydides.core.statistics.dao;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.statistics.model.TestRun;
import net.thucydides.core.statistics.model.TestRunTag;

import java.util.List;

public interface TestOutcomeHistoryDAO {
    List<TestRun> findAll();

    List<TestRun> findTestRunsByTitle(String title);

    void storeTestOutcomes(List<TestOutcome> testOutcomes);

    Long countTestRunsByTitle(String title);

    Long countTestRunsByTitleAndResult(String title, TestResult result);

    List<TestRunTag> findAllTags();

    List<TestRunTag> getLatestTagsForTestWithTitleByTitle(String title);

    List<TestResult> getResultsTestWithTitle(String title);

    List<TestResult> getResultsForTestsWithTag(String tag);

    List<TestResult> getResultsForTestsWithTagType(String tagType);

    Long countTestRunsByTag(String tag);

    Long countTestRunsByTagType(String tagType);

    Long countTestRunsByTagAndResult(String tag, TestResult result);

    Long countTestRunsByTagTypeAndResult(String tagType, TestResult result);

    List<TestRunTag> getLatestTagsForTestsWithTag(String tag);

    List<TestRunTag> getLatestTagsForTestsWithTagType(String tagType);

    List<String> findAllTagTypes();
}
