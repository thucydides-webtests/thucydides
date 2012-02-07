package net.thucydides.core.statistics.dao;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.statistics.model.TestOutcomeHistory;

import java.util.List;

/**
 * A description goes here.
 * User: johnsmart
 * Date: 7/02/12
 * Time: 3:51 PM
 */
public class TestStatisticsDAO {

    private final SystemClock clock;

    private List<TestOutcomeHistory> history = Lists.newArrayList();

    @Inject
    public TestStatisticsDAO(SystemClock clock) {
        this.clock = clock;
    }

    public List<TestOutcomeHistory> findAll() {
        return history;
    }

    public void storeTestOutcome(TestOutcome result) {
        history.add(TestOutcomeHistory.from(result).at(clock.getCurrentTime()));
    }
}
