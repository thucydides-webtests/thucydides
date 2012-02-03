package net.thucydides.core.statistics;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class WhenRecordingTestResultStatistics {

    StatisticsListener statisticsListener;

    TestStatistics testStatistics;

    @Before
    public void initListener() {
        MockitoAnnotations.initMocks(this);
        statisticsListener = new StatisticsListener();
        testStatistics = new TestStatistics();
    }
    @Test
    public void should_record_test_results_for_posterity() {



    }
}
