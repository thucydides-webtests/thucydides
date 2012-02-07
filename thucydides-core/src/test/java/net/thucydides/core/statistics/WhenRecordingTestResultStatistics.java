package net.thucydides.core.statistics;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.statistics.dao.TestStatisticsDAO;
import net.thucydides.core.statistics.model.TestHistory;
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

    TestStatisticsDAO testStatisticsDAO;

    @Before
    public void initListener() {
        MockitoAnnotations.initMocks(this);
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
        
        //List<TestHistory> storedTestHistories = testStatistics.getTestHistories();
        
        //assertThat(storedTestHistories.size(), is(1));
    }


    @Test
    public void should_be_able_to_find_the_average_number_of_failures_for_a_test() {
    }


    @Test
    public void should_be_able_to_find_the_mean_time_beween_failures_for_a_test() {

    }


}
