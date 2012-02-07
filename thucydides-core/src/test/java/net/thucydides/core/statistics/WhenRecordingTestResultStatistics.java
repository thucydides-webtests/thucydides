package net.thucydides.core.statistics;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.statistics.dao.DateProvider;
import net.thucydides.core.statistics.dao.TestStatisticsDAO;
import net.thucydides.core.statistics.model.TestOutcomeHistory;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class WhenRecordingTestResultStatistics {

    StatisticsListener statisticsListener;

    TestStatistics testStatistics;

    @Mock
    TestOutcome testOutcome;

    @Mock
    SystemClock clock;

    TestStatisticsDAO testStatisticsDAO;

    static final DateTime JANUARY_1ST_2012 = new DateTime(2012,1,1,0,0);

    @Before
    public void initListener() {
        MockitoAnnotations.initMocks(this);
        testStatisticsDAO = new TestStatisticsDAO(clock);
        statisticsListener = new StatisticsListener(testStatisticsDAO);
        testStatistics = new TestStatistics(testStatisticsDAO);

        // TODO: Set up statisticsListener to use a clean in-memory database
    }

    //
    // - title
    // - result
    // - execution date
    // -
    @Test
    public void should_record_test_results_for_posterity() {

        when(testOutcome.getResult()).thenReturn(TestResult.SUCCESS);

        statisticsListener.testFinished(testOutcome);
        
        List<TestOutcomeHistory> storedTestOutcomeHistories = testStatistics.getTestHistories();
        
        assertThat(storedTestOutcomeHistories.size(), is(1));
    }

    @Test
    public void should_record_the_execution_time_with_the_test_results() {

        when(testOutcome.getResult()).thenReturn(TestResult.SUCCESS);
        when(clock.getCurrentTime()).thenReturn(JANUARY_1ST_2012);

        statisticsListener.testFinished(testOutcome);

        List<TestOutcomeHistory> storedTestOutcomeHistories = testStatistics.getTestHistories();

        TestOutcomeHistory outcomeHistory = storedTestOutcomeHistories.get(0);
        assertThat(outcomeHistory.getExecutionDate(), is(JANUARY_1ST_2012));
    }


    @Test
    public void should_be_able_to_find_the_average_number_of_failures_for_a_test() {
    }


    @Test
    public void should_be_able_to_find_the_mean_time_beween_failures_for_a_test() {

    }


}
