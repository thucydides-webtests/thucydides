package net.thucydides.core.reports.history;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStepFactory;
import net.thucydides.core.reports.TestOutcomes;
import net.thucydides.core.util.ExtendedTemporaryFolder;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class WhenTrackingTestResultsOverTime {

    private TestHistory testHistory;

    private File homeDirectory;

    @Rule
    public TemporaryFolder temporaryFolder = new ExtendedTemporaryFolder();

    MockEnvironmentVariables environmentVariables;

    @Before
    public void prepareTestHistory() throws IOException {
        MockitoAnnotations.initMocks(this);
        environmentVariables = new MockEnvironmentVariables();

        homeDirectory = temporaryFolder.newFolder("home");
        environmentVariables.setProperty("user.home", homeDirectory.getAbsolutePath());

        testHistory = new TestHistory("project", environmentVariables);
        testHistory.clearHistory();

    }

    @Test
    public void history_should_be_stored_in_a_project_directory_in_the_dot_thucydides_directory_by_default() {
        File expectedDataDirectory = new File(new File(homeDirectory,".thucydides"),"project");
        assertThat(testHistory.getDirectory(), is(expectedDataDirectory));
    }

    @Test
    public void the_base_history_directory_can_be_overridden_using_a_system_property() throws IOException {

        File customHistoryDir = temporaryFolder.newFolder("history");

        environmentVariables.setProperty("thucydides.history", customHistoryDir.getAbsolutePath());
        testHistory = new TestHistory("project", environmentVariables);

        TestOutcomes results  = getResults();
        testHistory.updateData(results);
        testHistory.updateData(results);

        String[] historyFiles = new File(customHistoryDir,"project").list();

        assertThat(historyFiles.length, is(2));

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
