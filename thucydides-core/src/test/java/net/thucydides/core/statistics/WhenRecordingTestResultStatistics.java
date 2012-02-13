package net.thucydides.core.statistics;

import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.statistics.dao.TestOutcomeHistoryDAO;
import net.thucydides.core.statistics.model.TestRun;
import net.thucydides.core.statistics.model.TestRunTag;
import net.thucydides.core.statistics.model.TestStatistics;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import java.util.List;

import static net.thucydides.core.matchers.dates.DateMatchers.isSameAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
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

    @WithTag(value="Online sales", type="feature")
    class CarSalesTestCaseSample {
        @WithTag(value="Car sales", type="feature")
        public void car_sales_test() {}
    }

    @Before
    public void initListener() {
        MockitoAnnotations.initMocks(this);

        when(testOutcome.getTitle()).thenReturn("Some test");
        when(testOutcome.getResult()).thenReturn(TestResult.SUCCESS);
        when(testOutcome.getMethodName()).thenReturn("car_sales_test");
        when(testOutcome.getStoryTitle()).thenReturn("A Test Story");
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

        TestStatistics testStatistics = testStatisticsProvider.statisticsForTests(With.title("Boat sales test"));

        assertThat(testStatistics.getTotalTestRuns(), is(8L));
    }

    @Test
    public void should_be_able_to_find_the_total_number_of_passing_test_runs_for_a_given_test() {

        TestStatistics testStatistics = testStatisticsProvider.statisticsForTests(With.title("Boat sales test"));

        assertThat(testStatistics.getPassingTestRuns(), is(6L));
    }

    @Test
    public void should_be_able_to_find_the_average_pass_rate_for_a_given_test() {

        TestStatistics testStatistics = testStatisticsProvider.statisticsForTests(With.title("Boat sales test"));

        assertThat(testStatistics.getPassRate(), is(0.75));
    }

    @Test
    public void should_return_zero_for_pass_rate_if_no_tests_have_been_executed() {

        TestStatistics testStatistics = testStatisticsProvider.statisticsForTests(With.title("An unexecuted test"));

        assertThat(testStatistics.getPassRate(), is(0.0));
    }

    @WithTag(value="Online sales", type="feature")
    class SomeTestCaseWithTagOnMethodAndClass {
        @WithTag(value="Car sales", type="feature")
        public void some_test_method() {}
    }

    @Test
    public void should_record_associated_tags_with_a_test_run() {
        TestOutcome testOutcomeWithTags = TestOutcome.forTest("some_test_method", SomeTestCaseWithTagOnMethodAndClass.class);
        statisticsListener.testFinished(testOutcomeWithTags);

        List<TestRunTag> storedTags = testStatisticsProvider.findAllTags();
        assertThat(storedTags.isEmpty(), is(false));
    }

    @WithTag(value="Online sales", type="feature")
    class OnlineSalesTestCaseSample {
        @WithTag(value="Boat sales", type="feature")
        public void boat_sales_test() {}

        @WithTag(value="Car sales", type="feature")
        public void car_sales_test() {}

        @WithTag(value="House sales", type="feature")
        public void house_sales_test() {}

        @WithTag(value="Gizmo sales", type="feature")
        public void gizmo_sales_test() {}
    }

    @WithTag(value="Online sales", type="feature")
    class AnotherOnlineSalesTestCaseSample {
        @WithTag(value="Boat sales", type="feature")
        public void more_boat_sales_test() {}

        @WithTag(value="Car sales", type="feature")
        public void more_car_sales_test() {}
    }

    @Test
    public void should_retrieve_tags_associated_with_the_latest_test_run_of_a_test() {

        TestStatistics testStatistics = testStatisticsProvider.statisticsForTests(With.title("Boat sales test"));

        List<TestRunTag> storedTags = testStatistics.getTags();
        assertThat(storedTags.size(), is(2));
    }

    @Test
    public void should_retrieve_a_list_of_all_available_tags_associated_with_the_latest_test_run_of_a_test() {

        List<TestRunTag> allTags = testStatisticsProvider.findAllTags();

        assertThat(allTags.size(), is(5));
    }

    @Test
    @Ignore("In progress")
    public void should_retrieve_a_list_of_all_test_statistics_for_a_given_tag() {

        TestStatistics testStatistics = testStatisticsProvider.statisticsForTests(With.tag("Boat sales"));

        assertThat(testStatistics.getTotalTestRuns(), is(5L));
    }

    /*
        - should retrieve test statistics for a given tag
        - should find aggregate data for tests for a given tag
     */

    static boolean runOnce = false;

    private void prepareTestData() {
        if (!runOnce) {
            statisticsListener.testFinished(failingTestFor("boat_sales_test"));
            statisticsListener.testFinished(failingTestFor("car_sales_test"));
            statisticsListener.testFinished(failingTestFor("house_sales_test"));

            statisticsListener.testFinished(failingTestFor("boat_sales_test"));
            statisticsListener.testFinished(failingTestFor("car_sales_test"));
            statisticsListener.testFinished(passingTestFor("house_sales_test"));

            statisticsListener.testFinished(passingTestFor("boat_sales_test"));
            statisticsListener.testFinished(failingTestFor("car_sales_test"));
            statisticsListener.testFinished(failingTestFor("house_sales_test"));

            statisticsListener.testFinished(passingTestFor("boat_sales_test"));
            statisticsListener.testFinished(failingTestFor("car_sales_test"));
            statisticsListener.testFinished(passingTestFor("house_sales_test"));

            statisticsListener.testFinished(passingTestFor("boat_sales_test"));
            statisticsListener.testFinished(passingTestFor("car_sales_test"));
            statisticsListener.testFinished(failingTestFor("house_sales_test"));

            statisticsListener.testFinished(passingTestFor("boat_sales_test"));
            statisticsListener.testFinished(passingTestFor("car_sales_test"));
            statisticsListener.testFinished(passingTestFor("house_sales_test"));

            statisticsListener.testFinished(passingTestFor("boat_sales_test"));
            statisticsListener.testFinished(failingTestFor("car_sales_test"));
            statisticsListener.testFinished(failingTestFor("house_sales_test"));
            statisticsListener.testFinished(passingTestFor("gizmo_sales_test"));

            statisticsListener.testFinished(passingTestFor("boat_sales_test"));
            statisticsListener.testFinished(passingTestFor("car_sales_test"));
            statisticsListener.testFinished(passingTestFor("house_sales_test"));
            statisticsListener.testFinished(failingTestFor("gizmo_sales_test"));

            statisticsListener.testFinished(passingTestFor("more_boat_sales_test"));
            statisticsListener.testFinished(passingTestFor("more_car_sales_test"));

            statisticsListener.testFinished(passingTestFor("more_boat_sales_test"));
            statisticsListener.testFinished(passingTestFor("more_car_sales_test"));

            runOnce = true;
        }
    }

    private TestOutcome failingTestFor(String methodName) {
        TestOutcome failingTestOutcome = TestOutcome.forTest(methodName, OnlineSalesTestCaseSample.class);
        failingTestOutcome.setTestFailureCause(new AssertionError("A nasty bug"));
        return failingTestOutcome;
    }

    private TestOutcome passingTestFor(String methodName) {
        TestOutcome passingTestOutcome = TestOutcome.forTest(methodName, OnlineSalesTestCaseSample.class);
        passingTestOutcome.setAnnotatedResult(TestResult.SUCCESS);
        return passingTestOutcome;
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
