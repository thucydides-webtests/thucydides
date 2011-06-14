package net.thucydides.core.reports.integration;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.userstories.UserStoryLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static net.thucydides.core.matchers.UserStoryMatchers.containsTestsForStory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class WhenLoadingTestRunResultsIntoAUserStoryTestResults {

    UserStoryLoader loader;

    @Before
    public void createNewLoader() {
        loader = new UserStoryLoader();
    }

    @Test
    public void should_load_the_test_results_into_user_stories() throws IOException {
        
        List<StoryTestResults> stories = loader.loadFrom(new File("src/test/resources/single-user-story-reports"));
        assertThat(stories.size(), is(1));
        
        StoryTestResults singleStory = stories.get(0);
        assertThat(singleStory.getTestOutcomes().size(), is(2));
    }
    
    @Test
    public void should_load_multiple_user_stories_if_test_runs_have_more_than_one() throws IOException {
        
        List<StoryTestResults> stories = loader.loadFrom(new File("src/test/resources/multiple-user-story-reports"));
        assertThat(stories.size(), is(4));
        
        Story userStory1 = Story.withId("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.AUserStoryInAFeature", "A user story in a feature");
        Story userStory2 = Story.withId("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.AnotherUserStoryInAFeature", "Another user story in a feature");
        Story userStory3 = Story.withId("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.YetAnotherUserStory", "Yet another user story");
        assertThat(stories, containsTestsForStory(userStory1));
        assertThat(stories, containsTestsForStory(userStory2));
        assertThat(stories, containsTestsForStory(userStory3));
    }    
    
    
}
