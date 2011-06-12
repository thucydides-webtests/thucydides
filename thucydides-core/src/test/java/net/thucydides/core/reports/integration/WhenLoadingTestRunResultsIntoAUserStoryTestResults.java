package net.thucydides.core.reports.integration;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.UserStoryTestResults;
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
        
        List<UserStoryTestResults> userStories = loader.loadStoriesFrom(new File("src/test/resources/single-user-story-reports"));            
        assertThat(userStories.size(), is(1));
        
        UserStoryTestResults singleUserStory = userStories.get(0);
        assertThat(singleUserStory.getTestOutcomes().size(), is(2));
    }
    
    @Test
    public void should_load_multiple_user_stories_if_test_runs_have_more_than_one() throws IOException {
        
        List<UserStoryTestResults> userStories = loader.loadStoriesFrom(new File("src/test/resources/multiple-user-story-reports"));            
        assertThat(userStories.size(), is(3));
        
        Story userStory1 = Story.withId("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.AUserStoryInAFeature", "A user story in a feature");
        Story userStory2 = Story.withId("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.AnotherUserStoryInAFeature", "Another user story in a feature");
        Story userStory3 = Story.withId("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AFeature.YetAnotherUserStory", "Yet another user story");
        assertThat(userStories, containsTestsForStory(userStory1));
        assertThat(userStories, containsTestsForStory(userStory2));
        assertThat(userStories, containsTestsForStory(userStory3));
    }    
    
    
}
