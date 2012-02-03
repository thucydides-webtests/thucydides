package net.thucydides.core.statistics;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class WhenRecordingTestResultStatistics {

    StatisticsListener statisticsListener;

    @Before
    public void initListener() {
        MockitoAnnotations.initMocks(this);

    }
    @Test
    public void should_record_test_results_for_posterity() {

    }
}
