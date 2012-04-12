package net.thucydides.core.statistics;

import net.thucydides.core.statistics.model.TestRun;
import net.thucydides.core.statistics.model.TestRunTag;
import net.thucydides.core.statistics.model.TestStatistics;

import java.util.List;

/**
 * A description goes here.
 * User: johnsmart
 * Date: 9/04/12
 * Time: 10:52 PM
 */
public interface TestStatisticsProvider {
    List<TestRun> testRunsForTest(With withCondition);

    List<TestRun> getAllTestHistories();

    TestStatistics statisticsForTests(With withCondition);

    List<TestRunTag> findAllTags();

    List<String> findAllTagTypes();
}
