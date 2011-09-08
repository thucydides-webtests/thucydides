package net.thucydides.core.reports.history;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStepFactory;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.reports.html.history.TestResultSnapshot;
import net.thucydides.core.util.EnvironmentVariables;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

public class WhenTrackingTestResultsOverTime {

    private TestHistory testHistory;
    private String originalUserHomeDirectory;

    private File homeDirectory;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public SaveWebdriverSystemPropertiesRule saveWebdriverSystemPropertiesRule = new SaveWebdriverSystemPropertiesRule();

    @Mock
    EnvironmentVariables environmentVariables;

    @Before
    public void prepareTestHistory() {
        MockitoAnnotations.initMocks(this);

        originalUserHomeDirectory = System.getProperty("user.home");
        homeDirectory = temporaryFolder.newFolder("home");
        System.setProperty("user.home", homeDirectory.getAbsolutePath());

        testHistory = new TestHistory("project");
        testHistory.setEnvironmentVariables(environmentVariables);

    }

    @After
    public void restoreHomeDirectory() {
        System.setProperty("user.home", originalUserHomeDirectory);
    }

    @Test
    public void history_should_be_stored_in_a_project_directory_in_the_dot_thucydides_directory_by_default() {
        File expectedDataDirectory = new File(new File(homeDirectory,".thucydides"),"project");
        assertThat(testHistory.getDirectory(), is(expectedDataDirectory));
    }

    @Test
    public void the_base_history_directory_can_be_overridden_using_a_system_property() {

        File customHistoryDir = temporaryFolder.newFolder("history");

        System.setProperty("thucydides.history", customHistoryDir.getAbsolutePath());
        testHistory = new TestHistory("project");

        List<FeatureResults> results = getResults();
        testHistory.updateData(results);
        testHistory.updateData(results);

        String[] historyFiles = new File(customHistoryDir,"project").list();

        assertThat(historyFiles.length, is(2));

    }

    @Test
    public void should_store_a_new_set_of_timestamped_results() {

        List<FeatureResults> results = getResults();
        testHistory.updateData(results);

        List<TestResultSnapshot> data = testHistory.getHistory();
        assertThat(data.size(), is(1));
        assertThat(data.get(0).getFailingSteps(), is(90));
        assertThat(data.get(0).getPassingSteps(), is(30));
        assertThat(data.get(0).getSkippedSteps(), is(10));
        assertThat(data.get(0).getSpecifiedSteps(), is(225));
    }

    @Test
    public void should_record_the_build_number_if_present() {

        Mockito.when(environmentVariables.getValue(eq("BUILD_ID"), anyString())).thenReturn("123");
        List<FeatureResults> results = getResults();
        testHistory.updateData(results);

        List<TestResultSnapshot> data = testHistory.getHistory();

        assertThat(data.size(), is(1));
        assertThat(data.get(0).getBuildId(), is("123"));


    }

    @Test
    public void by_default_the_build_id_is_marked_as_manual() {

        Mockito.when(environmentVariables.getValue("BUILD_ID","MANUAL")).thenReturn("MANUAL");

        List<FeatureResults> results = getResults();
        testHistory.updateData(results);

        List<TestResultSnapshot> data = testHistory.getHistory();
        assertThat(data.size(), is(1));
        assertThat(data.get(0).getBuildId(), is("MANUAL"));
    }


    @Test
    public void should_store_successive_sets_of_timestamped_results() {

        List<FeatureResults> results = getResults();
        testHistory.updateData(results);
        testHistory.updateData(results);
        testHistory.updateData(results);

        List<TestResultSnapshot> data = testHistory.getHistory();
        assertThat(data.size(), is(3));
    }

    @Test
    public void should_load_historical_data_in_chronological_order() {

        List<FeatureResults> results = getResults();
        testHistory.updateData(results);
        testHistory.updateData(results);
        testHistory.updateData(results);
        testHistory.updateData(results);

        List<TestResultSnapshot> data = testHistory.getHistory();
        assertThat(data.get(0).getTime().getMillis(), lessThan(data.get(1).getTime().getMillis()));
        assertThat(data.get(1).getTime().getMillis(), lessThan(data.get(2).getTime().getMillis()));
        assertThat(data.get(2).getTime().getMillis(), lessThan(data.get(3).getTime().getMillis()));
    }

    @Test
    public void should_clear_historical_data_if_requested() {

        List<FeatureResults> results = getResults();
        testHistory.updateData(results);
        testHistory.updateData(results);
        testHistory.updateData(results);
        testHistory.updateData(results);

        List<TestResultSnapshot> data = testHistory.getHistory();
        assertThat(data.size(), greaterThan(0));

        testHistory.clearHistory();

        data = testHistory.getHistory();
        assertThat(data.size(), is(0));
    }

    private List<FeatureResults> getResults() {
        Story story = Story.from(WidgetFeature.PurchaseNewWidget.class);
        StoryTestResults storyResults = new StoryTestResults(story);
        storyResults.recordTestRun(thatSucceedsFor(story, 10));
        storyResults.recordTestRun(thatSucceedsFor(story, 20));
        storyResults.recordTestRun(thatIsFailingFor(story, 30));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));
        storyResults.recordTestRun(thatIsPendingFor(story, 0));

        StoryTestResults storyResults2 = new StoryTestResults(story);
        storyResults2.recordTestRun(thatIsFailingFor(story, 10));
        storyResults2.recordTestRun(thatIsFailingFor(story, 20));
        storyResults2.recordTestRun(thatIsFailingFor(story, 30));
        storyResults2.recordTestRun(thatIsIgnoredFor(story, 10));
        storyResults2.recordTestRun(thatIsPendingFor(story, 0));
        storyResults2.recordTestRun(thatIsPendingFor(story, 0));

        FeatureResults featureResults = new FeatureResults(ApplicationFeature.from(WidgetFeature.class));
        featureResults.recordStoryResults(storyResults);
        featureResults.recordStoryResults(storyResults2);

        return Arrays.asList(featureResults);
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
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsPendingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(TestStepFactory.pendingTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsIgnoredFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(TestStepFactory.ignoredTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsFailingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(TestStepFactory.failingTestStepCalled("Step " + i, new AssertionError()));
        }
        return testOutcome;
    }

}
