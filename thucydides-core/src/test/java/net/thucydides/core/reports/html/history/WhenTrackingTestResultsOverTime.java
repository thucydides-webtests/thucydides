package net.thucydides.core.reports.html.history;

import net.thucydides.core.annotations.Feature;
import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.reports.history.TestHistory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static net.thucydides.core.model.TestStepFactory.failingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.ignoredTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.pendingTestStepCalled;
import static net.thucydides.core.model.TestStepFactory.successfulTestStepCalled;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class WhenTrackingTestResultsOverTime {

    private TestHistory testHistory;
    private String originalUserHomeDirectory;

    private File homeDirectory;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void prepareTestHistory() {
        originalUserHomeDirectory = System.getProperty("user.home");
        homeDirectory = temporaryFolder.newFolder("home");

        System.setProperty("user.home", homeDirectory.getAbsolutePath());
        testHistory = new TestHistory("project");

    }

    @After
    public void restoreHomeDirectory() {
        System.setProperty("user.home", originalUserHomeDirectory);
    }

    @Test
    public void history_should_be_stored_in_the_dot_thucydides_directory_by_default() {
        File expectedDataDirectory = new File(homeDirectory,".thucydides");
        assertThat(testHistory.getDirectory(), is(expectedDataDirectory));
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
            testOutcome.recordStep(successfulTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsPendingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(pendingTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsIgnoredFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(ignoredTestStepCalled("Step " + i));
        }
        return testOutcome;
    }

    private TestOutcome thatIsFailingFor(Story story, int stepCount) {
        TestOutcome testOutcome = TestOutcome.forTestInStory("a test", story);
        for(int i = 1; i <= stepCount; i++ ){
            testOutcome.recordStep(failingTestStepCalled("Step " + i, new AssertionError()));
        }
        return testOutcome;
    }

}
