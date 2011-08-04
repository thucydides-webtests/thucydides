package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static java.awt.Color.BLUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class WhenDeterminingTheColorOfAFeatureByDegreeOfCompleteness extends AbstractColorSchemeTest {

    ColorScheme colorScheme;

    private static final Color PALE_BLUE = ProgressColorScheme.PALE_BLUE;
    private static final Color BRIGHT_BLUE = ProgressColorScheme.BRIGHT_BLUE;
    private static final Color MEDIUM_BLUE = new Color(117,193,255);

    @Before
    public void createColorScheme() {
        colorScheme = new ProgressColorScheme();
    }

    @Test
    public void a_successful_test_step_should_be_bright_blue() {
        Color color = colorScheme.colorFor(mockTestStep(TestResult.SUCCESS));
        assertThat(color, is(BRIGHT_BLUE));
    }

    @Test
    public void a_failing_test_step_should_be_page_blue() {
        Color color = colorScheme.colorFor(mockTestStep(TestResult.FAILURE));
        assertThat(color, is(PALE_BLUE));
    }

    @Test
    public void a_pending_test_step_should_be_page_blue() {
        Color color = colorScheme.colorFor(mockTestStep(TestResult.PENDING));
        assertThat(color, is(PALE_BLUE));
    }

    @Test
    public void a_skipped_test_step_should_be_page_blue() {
        Color color = colorScheme.colorFor(mockTestStep(TestResult.SKIPPED));
        assertThat(color, is(PALE_BLUE));
    }

    @Test
    public void an_ignored_test_step_should_be_page_blue() {
        Color color = colorScheme.colorFor(mockTestStep(TestResult.IGNORED));
        assertThat(color, is(PALE_BLUE));
    }

    @Test
    public void a_test_outcome_with_no_passing_should_be_page_blue() {
        Color color = colorScheme.colorFor(mockTestStep(TestResult.IGNORED));
        assertThat(color, is(PALE_BLUE));
    }

    @Test
    public void a_test_outcome_with_no_passing_tests_should_be_pale_blue() {
        TestOutcome outcome = mockTestOutcome(10, 0, TestResult.PENDING);
        Color color = colorScheme.colorFor(outcome);

        assertThat(color, is(PALE_BLUE));
    }

    @Test
    public void a_test_outcome_with_all_passing_tests_should_be_bright_blue() {
        TestOutcome outcome = mockTestOutcome(10, 10, TestResult.SUCCESS);
        Color color = colorScheme.colorFor(outcome);

        assertThat(color, is(BRIGHT_BLUE));
    }

    @Test
    public void a_test_outcome_with_some_passing_tests_should_be_medium_blue() {
        TestOutcome outcome = mockTestOutcome(10, 10, TestResult.SUCCESS);
        Color color = colorScheme.colorFor(outcome);

        assertThat(color, is(BRIGHT_BLUE));
    }

     @Test
     public void a_completely_failing_story_should_be_pale_blue() {
         StoryTestResults story = mockStory(20, 0,0,20);
         Color color = colorScheme.colorFor(story);

         assertThat(color, is(PALE_BLUE));
     }

    @Test
    public void a_completely_passing_story_should_be_bright_blue() {
        StoryTestResults story = mockStory(20, 20,0,0);
        Color color = colorScheme.colorFor(story);

        assertThat(color, is(BRIGHT_BLUE));
    }

    @Test
    public void a_partially_passing_story_should_be_medium_blue() {
        StoryTestResults story = mockStory(20, 10,0,10);
        Color color = colorScheme.colorFor(story);

        assertThat(color, is(MEDIUM_BLUE));
    }

    @Test
    public void a_partially_pending_story_should_be_medium_blue() {
        StoryTestResults story = mockStory(20, 10,10,0);
        Color color = colorScheme.colorFor(story);

        assertThat(color, is(MEDIUM_BLUE));
    }


    @Test
    public void a_feature_with_no_passing_tests_should_be_pale_blue() {
        FeatureResults feature = mockFeatureResults(WidgetFeature.class,1000, 10, 0, 0,0,0);
        Color color = colorScheme.colorFor(feature);

        assertThat(color, is(PALE_BLUE));
    }

    @Test
    public void a_feature_with_all_passing_tests_should_be_bright_blue() {
        FeatureResults feature = mockFeatureResults(WidgetFeature.class,500, 10, 100, 100,0,0);
        Color color = colorScheme.colorFor(feature);

        assertThat(color, is(BRIGHT_BLUE));
    }

    @Test
    public void a_feature_with_some_passing_tests_should_be_bright_blue() {
        FeatureResults feature = mockFeatureResults(WidgetFeature.class,500, 10, 100, 50,0,0);
        Color color = colorScheme.colorFor(feature);

        assertThat(color, is(MEDIUM_BLUE));
    }
}