package net.thucydides.core.statistics;

import com.google.inject.Inject;
import net.thucydides.core.statistics.dao.TestOutcomeHistoryDAO;
import net.thucydides.core.statistics.model.TestRun;
import net.thucydides.core.statistics.model.TestRunTag;
import net.thucydides.core.statistics.model.TestStatistics;
import net.thucydides.core.statistics.service.WithTagNamed;

import java.util.List;

/**
 * A description goes here.
 * User: johnsmart
 * Date: 3/02/12
 * Time: 4:04 PM
 */
public class TestStatisticsProvider {

    private final TestOutcomeHistoryDAO testOutcomeHistoryDAO;

    @Inject
    public TestStatisticsProvider(TestOutcomeHistoryDAO testOutcomeHistoryDAO) {
        this.testOutcomeHistoryDAO = testOutcomeHistoryDAO;
    }

    public List<TestRun> testRunsForTest(With withCondition) {
        if (withCondition instanceof WithTitle) {
            return testOutcomeHistoryDAO.findTestRunsByTitle(((WithTitle) withCondition).getTitle());
        }
        return null;
    }

    public List<TestRun> getAllTestHistories() {
        return testOutcomeHistoryDAO.findAll();
    }


    public TestStatistics statisticsForTests(With withCondition) {
        if (withCondition instanceof WithTitle) {
            return testStatisticsForTitle((WithTitle) withCondition);
        } else if (withCondition instanceof WithTagNamed) {
            return testStatisticsForTestsWithTag((WithTagNamed) withCondition);
        }
        return null;
    }

    private TestStatistics testStatisticsForTestsWithTag(WithTagNamed withCondition) {
        String tag = withCondition.getTag();
        Long totalTests = testOutcomeHistoryDAO.countTestRunsByTag(tag);
        Long passingTests = testOutcomeHistoryDAO.countPassingTestRunsByTag(tag);
        List<TestRunTag> latestTags = testOutcomeHistoryDAO.getLatestTagsForTestWithTag(tag);
        return new TestStatistics(totalTests, passingTests, latestTags);
    }

    private TestStatistics testStatisticsForTitle(WithTitle withCondition) {
        Long totalTests = testOutcomeHistoryDAO.countTestRunsByTitle(((WithTitle) withCondition).getTitle());
        Long passingTests = testOutcomeHistoryDAO.countPassingTestRunsByTitle(((WithTitle) withCondition).getTitle());
        List<TestRunTag> latestTags = testOutcomeHistoryDAO.getLatestTagsForTestWithTitleByTitle(((WithTitle) withCondition).getTitle());
        return new TestStatistics(totalTests, passingTests, latestTags);
    }

    public List<TestRunTag> findAllTags() {
        return testOutcomeHistoryDAO.findAllTags();
    }
}
