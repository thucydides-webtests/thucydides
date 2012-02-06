package net.thucydides.core.statistics;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.steps.StepFailure;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class WhenRecordingTestResultStatistics {

    StatisticsListener statisticsListener;

    TestStatistics testStatistics;

    @Mock
    TestOutcome testOutcome;

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
