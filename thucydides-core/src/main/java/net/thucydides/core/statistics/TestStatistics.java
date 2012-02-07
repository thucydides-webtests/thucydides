package net.thucydides.core.statistics;

import com.google.inject.Inject;
import net.thucydides.core.statistics.dao.TestStatisticsDAO;
import net.thucydides.core.statistics.model.TestOutcomeHistory;

import java.util.List;

/**
 * A description goes here.
 * User: johnsmart
 * Date: 3/02/12
 * Time: 4:04 PM
 */
public class TestStatistics {

    private final  TestStatisticsDAO testStatisticsDAO;

    @Inject
    public TestStatistics(TestStatisticsDAO testStatisticsDAO) {
        this.testStatisticsDAO = testStatisticsDAO;
    }

    public List<TestOutcomeHistory> getTestHistories() {
        return testStatisticsDAO.findAll();
    }
}
