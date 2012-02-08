package net.thucydides.core.statistics;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.statistics.dao.TestOutcomeHistoryDAO;
import net.thucydides.core.statistics.model.TestRun;
import net.thucydides.core.statistics.model.TestStatistics;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;

import java.util.List;

import static net.thucydides.core.matchers.dates.DateMatchers.isSameAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenRecordingTestResultStatistics {


    /*
        - Retrieve the list of the executed tests
        - Retrieve statistics about each of the tests
        - Retrieve detailed execution stats for a particular test
     */
    StatisticsListener statisticsListener;

    TestStatisticsProvider testStatisticsProvider;

    @Mock
    TestOutcome testOutcome;

    @Mock
    TestOutcome anotherTestOutcome;

    @Mock
    SystemClock clock;

    TestOutcomeHistoryDAO testOutcomeHistoryDAO;

    static final DateTime JANUARY_1ST_2012 = new DateTime(2012, 1, 1, 0, 0);

    @Before
    public void initListener() {
        MockitoAnnotations.initMocks(this);

        when(testOutcome.getTitle()).thenReturn("Some test");
        when(testOutcome.getResult()).thenReturn(TestResult.SUCCESS);
        when(testOutcome.getMethodName()).thenReturn("someTest");
        when(testOutcome.getDuration()).thenReturn(500L);

        testOutcomeHistoryDAO = Injectors.getInjector().getInstance(TestOutcomeHistoryDAO.class);
        statisticsListener = new StatisticsListener(testOutcomeHistoryDAO);
        testStatisticsProvider = new TestStatisticsProvider(testOutcomeHistoryDAO);

        prepareTestData();
    }

    @Test
    public void should_record_test_results_for_posterity() {

        prepareDAOWithFixedClock();

        when(testOutcome.getResult()).thenReturn(TestResult.SUCCESS);

        statisticsListener.testFinished(testOutcome);

        List<TestRun> storedTestRuns = testStatisticsProvider.testRunsForTest(With.title(testOutcome.getTitle()));
        assertThat(storedTestRuns.size(), greaterThan(0));

        TestRun lastTestRun = storedTestRuns.get(storedTestRuns.size() - 1);
        assertThat(lastTestRun.getTitle(), is(testOutcome.getTitle()));
        assertThat(lastTestRun.getExecutionDate(), isSameAs(JANUARY_1ST_2012.toDate()));
        assertThat(lastTestRun.getDuration(), is(testOutcome.getDuration()));
    }


    @Test
    public void should_be_able_to_find_the_total_number_of_test_runs_for_a_given_test() {

        TestStatistics testStatistics = testStatisticsProvider.statisticsForTests(With.title("A test with failures"));

        assertThat(testStatistics.getTotalTestRuns(), is(10L));
    }

    @Test
    public void should_be_able_to_find_the_total_number_of_passing_test_runs_for_a_given_test() {

        TestStatistics testStatistics = testStatisticsProvider.statisticsForTests(With.title("A test with failures"));

        assertThat(testStatistics.getPassingTestRuns(), is(7L));
    }

    boolean runOnce = false;

    private void prepareTestData() {
        if (!runOnce) {
            statisticsListener.testFinished(failingTestCalled("A test with failures", "a_test_with_failures"));
            statisticsListener.testFinished(failingTestCalled("A test with failures", "a_test_with_failures"));
            statisticsListener.testFinished(failingTestCalled("A test with failures", "a_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("A test with failures", "a_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("A test with failures", "a_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("A test with failures", "a_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("A test with failures", "a_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("A test with failures", "a_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("A test with failures", "a_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("A test with failures", "a_test_with_failures"));

            statisticsListener.testFinished(failingTestCalled("Another test with failures", "another_test_with_failures"));
            statisticsListener.testFinished(failingTestCalled("Another test with failures", "another_test_with_failures"));
            statisticsListener.testFinished(failingTestCalled("Another test with failures", "another_test_with_failures"));
            statisticsListener.testFinished(failingTestCalled("Another test with failures", "another_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("Another test with failures", "another_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("Another test with failures", "another_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("Another test with failures", "another_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("Another test with failures", "another_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("Another test with failures", "another_test_with_failures"));
            statisticsListener.testFinished(successfulTestCalled("Another test with failures", "another_test_with_failures"));

            runOnce = true;
        }
        statisticsListener.testFinished(testOutcome);

    }

    private TestOutcome failingTestCalled(String title, String methodName) {
        TestOutcome failingTestOutcome = mock(TestOutcome.class);
        return testCalled(title, methodName, failingTestOutcome, TestResult.FAILURE);
    }

    private TestOutcome successfulTestCalled(String title, String methodName) {
        TestOutcome failingTestOutcome = mock(TestOutcome.class);
        return testCalled(title, methodName, failingTestOutcome, TestResult.SUCCESS);
    }

    private TestOutcome testCalled(String title, String methodName, TestOutcome failingTestOutcome, TestResult testResult) {
        when(failingTestOutcome.getTitle()).thenReturn(title);
        when(failingTestOutcome.getResult()).thenReturn(testResult);
        when(failingTestOutcome.getMethodName()).thenReturn(methodName);
        when(failingTestOutcome.getDuration()).thenReturn(500L);

        return failingTestOutcome;
    }

    private void prepareDAOWithFixedClock() {
        when(clock.getCurrentTime()).thenReturn(JANUARY_1ST_2012);
        testOutcomeHistoryDAO = new TestOutcomeHistoryDAO(Injectors.getInjector().getInstance(EntityManager.class), clock);
        statisticsListener = new StatisticsListener(testOutcomeHistoryDAO);
        testStatisticsProvider = new TestStatisticsProvider(testOutcomeHistoryDAO);
    }
}
