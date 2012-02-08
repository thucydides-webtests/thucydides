package net.thucydides.core.statistics;

import com.google.inject.Inject;
import net.thucydides.core.statistics.dao.TestOutcomeHistoryDAO;
import net.thucydides.core.statistics.model.TestRun;
import net.thucydides.core.statistics.model.TestStatistics;

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
            Long totalTests = testOutcomeHistoryDAO.countTestRunsByTitle(((WithTitle) withCondition).getTitle());
            Long passingTests = testOutcomeHistoryDAO.countPassingTestRunsByTitle(((WithTitle) withCondition).getTitle());
            return new TestStatistics(totalTests, passingTests);
        }
        return null;
    }

}
