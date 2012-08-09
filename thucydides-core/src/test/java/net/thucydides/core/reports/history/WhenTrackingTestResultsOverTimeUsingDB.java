package net.thucydides.core.reports.history;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.thucydides.core.annotations.Feature;
import net.thucydides.core.guice.DatabaseConfig;
import net.thucydides.core.guice.EnvironmentVariablesDatabaseConfig;
import net.thucydides.core.guice.ThucydidesModule;
import net.thucydides.core.logging.ThucydidesLogging;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStepFactory;
import net.thucydides.core.pages.InternalSystemClock;
import net.thucydides.core.pages.SystemClock;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.reports.html.history.JPATestResultSnapshotDAO;
import net.thucydides.core.reports.html.history.TestResultSnapshot;
import net.thucydides.core.reports.html.history.TestResultSnapshotDAO;
import net.thucydides.core.statistics.Statistics;
import net.thucydides.core.statistics.StatisticsListener;
import net.thucydides.core.statistics.dao.JPATestOutcomeHistoryDAO;
import net.thucydides.core.statistics.dao.TestOutcomeHistoryDAO;
import net.thucydides.core.statistics.service.ClasspathTagProviderService;
import net.thucydides.core.statistics.service.TagProviderService;
import net.thucydides.core.steps.ConsoleLoggingListener;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.SystemPropertiesConfiguration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class WhenTrackingTestResultsOverTimeUsingDB {

    private TestHistoryInDatabase testHistory;

    Injector injector;
    ThucydidesModuleWithMockEnvironmentVariables guiceModule;
    MockEnvironmentVariables environmentVariables;


    class ThucydidesModuleWithMockEnvironmentVariables extends ThucydidesModule {
        @Override
        protected void configure() {
            clearEntityManagerCache();
            bind(SystemClock.class).to(InternalSystemClock.class).in(Singleton.class);
            bind(EnvironmentVariables.class).to(MockEnvironmentVariables.class).in(Singleton.class);
            bind(DatabaseConfig.class).to(EnvironmentVariablesDatabaseConfig.class).in(Singleton.class);
            bind(TestOutcomeHistoryDAO.class).to(JPATestOutcomeHistoryDAO.class);
            bind(StepListener.class).annotatedWith(Statistics.class).to(StatisticsListener.class);
            bind(StepListener.class).annotatedWith(ThucydidesLogging.class).to(ConsoleLoggingListener.class);
            bind(TagProviderService.class).to(ClasspathTagProviderService.class).in(Singleton.class);
            bind(Configuration.class).to(SystemPropertiesConfiguration.class).in(Singleton.class);
            bind(TestResultSnapshotDAO.class).to(JPATestResultSnapshotDAO.class).in(Singleton.class);
        }
    }

    @Before
    public void prepareTestHistory() {
        MockitoAnnotations.initMocks(this);

        guiceModule = new ThucydidesModuleWithMockEnvironmentVariables();
        injector = Guice.createInjector(guiceModule);
        environmentVariables = injector.getInstance(MockEnvironmentVariables.class);
        TestResultSnapshotDAO testResultSnapshotDAO = injector.getInstance(TestResultSnapshotDAO.class);

        testHistory = new TestHistoryInDatabase(environmentVariables, testResultSnapshotDAO);
        testHistory.clearHistory();

    }

    @Test
    public void should_store_a_new_set_of_timestamped_results() {

        TestOutcomes results  = getResults();
        testHistory.updateData(results);

        List<TestResultSnapshot> data = testHistory.getHistory();
        assertThat(data.size(), is(1));
        assertThat(data.get(0).getFailingSteps(), is(90));
        assertThat(data.get(0).getPassingSteps(), is(30));
        assertThat(data.get(0).getSkippedSteps(), is(10));
        assertThat(data.get(0).getSpecifiedSteps(), is(130));
    }


    @Test
    public void snapshots_should_be_ordered_by_date() {
        TestResultSnapshot snapshot1 = new TestResultSnapshot(new DateTime(2000,01,01,01,01,01), 0,0,0,0,"MANUAL");
        TestResultSnapshot snapshot2 = new TestResultSnapshot(new DateTime(2000,01,01,01,01,01), 0,0,0,0,"MANUAL");

        assertThat(snapshot1.compareTo(snapshot2), is(0));
    }

    @Test
    public void a_snapshot_should_be_equal_to_itself() {
        TestResultSnapshot snapshot = new TestResultSnapshot(new DateTime(2000,01,01,01,01,01), 0,0,0,0,"MANUAL");
        assertThat(snapshot.compareTo(snapshot), is(0));
    }

    @Test
    public void snapshots_should_be_ordered_by_date_with_inferior_date() {
        TestResultSnapshot snapshot1 = new TestResultSnapshot(new DateTime(2000,01,01,01,01,01), 0,0,0,0,"MANUAL");
        TestResultSnapshot snapshot2 = new TestResultSnapshot(new DateTime(1999,01,01,01,01,01), 0,0,0,0,"MANUAL");

        assertThat(snapshot1.compareTo(snapshot2), is(1));
    }

    @Test
    public void snapshots_should_be_ordered_by_date_with_superior_date() {
        TestResultSnapshot snapshot1 = new TestResultSnapshot(new DateTime(2000,01,01,01,01,01), 0,0,0,0,"MANUAL");
        TestResultSnapshot snapshot2 = new TestResultSnapshot(new DateTime(2001,01,01,01,01,01), 0,0,0,0,"MANUAL");

        assertThat(snapshot1.compareTo(snapshot2), is(-1));
    }

    @Test
    public void should_record_the_build_number_if_present() {

        environmentVariables.setValue("BUILD_ID","123");
        TestOutcomes results = getResults();
        testHistory.updateData(results);

        List<TestResultSnapshot> data = testHistory.getHistory();

        assertThat(data.size(), is(1));
        assertThat(data.get(0).getBuildId(), is("123"));


    }

    @Test
    public void by_default_the_build_id_is_marked_as_manual() {

        environmentVariables.setValue("BUILD_ID","MANUAL");

        TestOutcomes results = getResults();
        testHistory.updateData(results);

        List<TestResultSnapshot> data = testHistory.getHistory();
        assertThat(data.size(), is(1));
        assertThat(data.get(0).getBuildId(), is("MANUAL"));
    }


    @Test
    public void should_store_successive_sets_of_timestamped_results() {

        TestOutcomes results = getResults();
        testHistory.updateData(results);
        waitMilliseconds(10);
        testHistory.updateData(results);
        waitMilliseconds(10);
        testHistory.updateData(results);

        List<TestResultSnapshot> data = testHistory.getHistory();
        assertThat(data.size(), is(3));
    }

    private void waitMilliseconds(int pauseInMilliseconds) {
        try {
            Thread.sleep(pauseInMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void should_load_historical_data_in_chronological_order() {

        TestOutcomes results = getResults();
        testHistory.updateData(results);
        waitMilliseconds(10);
        testHistory.updateData(results);
        waitMilliseconds(10);
        testHistory.updateData(results);
        waitMilliseconds(10);
        testHistory.updateData(results);

        List<TestResultSnapshot> data = testHistory.getHistory();
        assertThat(data.get(0).getTime().getMillis(), lessThan(data.get(1).getTime().getMillis()));
        assertThat(data.get(1).getTime().getMillis(), lessThan(data.get(2).getTime().getMillis()));
        assertThat(data.get(2).getTime().getMillis(), lessThan(data.get(3).getTime().getMillis()));
    }

    @Test
    public void should_clear_historical_data_if_requested() {

        TestOutcomes results = getResults();
        testHistory.updateData(results);
        waitMilliseconds(10);
        testHistory.updateData(results);
        waitMilliseconds(10);
        testHistory.updateData(results);
        waitMilliseconds(10);
        testHistory.updateData(results);
        waitMilliseconds(10);

        List<TestResultSnapshot> data = testHistory.getHistory();
        assertThat(data.size(), greaterThan(0));

        testHistory.clearHistory();

        data = testHistory.getHistory();
        assertThat(data.size(), is(0));
    }

    private TestOutcomes getResults() {
        List<TestOutcome> testOutcomeList = new ArrayList<TestOutcome>();

        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        testOutcomeList.add(thatSucceedsFor(story, 10));
        testOutcomeList.add(thatSucceedsFor(story, 20));
        testOutcomeList.add(thatIsFailingFor(story, 30));
        testOutcomeList.add(thatIsPendingFor(story, 0));
        testOutcomeList.add(thatIsPendingFor(story, 0));
        testOutcomeList.add(thatIsPendingFor(story, 0));

        testOutcomeList.add(thatIsFailingFor(story, 10));
        testOutcomeList.add(thatIsFailingFor(story, 20));
        testOutcomeList.add(thatIsFailingFor(story, 30));
        testOutcomeList.add(thatIsIgnoredFor(story, 10));
        testOutcomeList.add(thatIsPendingFor(story, 0));
        testOutcomeList.add(thatIsPendingFor(story, 0));

        return TestOutcomes.of(testOutcomeList);
    }


    @Feature
    class WidgetFeature {
         class PurchaseNewWidget{};
         class SearchWidgets{};
         class DisplayWidgets{};
    }

    private TestOutcome thatSucceedsFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(TestStepFactory.forASuccessfulTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsPendingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(TestStepFactory.forAPendingTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsIgnoredFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(TestStepFactory.forAnIgnoredTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsFailingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(TestStepFactory.forAFailingTestStepCalled("Step " + i, new AssertionError()));
        }
        return testOutcome;
    }

}
