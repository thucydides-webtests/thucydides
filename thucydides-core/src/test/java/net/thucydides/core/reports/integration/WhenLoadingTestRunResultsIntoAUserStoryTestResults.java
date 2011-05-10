package net.thucydides.core.reports.integration;

import static net.thucydides.core.matchers.UserStoryMatchers.containsTestsForStory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.thucydides.core.model.UserStory;
import net.thucydides.core.model.UserStoryTestResults;
import net.thucydides.core.model.loaders.UserStoryLoader;

import org.junit.Before;
import org.junit.Test;


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
        assertThat(singleUserStory.getTestRuns().size(), is(2));
    }
    
    @Test
    public void should_load_multiple_user_stories_if_test_runs_have_more_than_one() throws IOException {
        
        List<UserStoryTestResults> userStories = loader.loadStoriesFrom(new File("src/test/resources/multiple-user-story-reports"));            
        assertThat(userStories.size(), is(3));
        
        UserStory userStory1 = new UserStory("A user story", "US1", "some.user.stories.UserStory1");
        UserStory userStory2 = new UserStory("Another user story", "US2", "some.user.stories.UserStory2");
        UserStory userStory3 = new UserStory("Yet another user story", "US3", "some.user.stories.UserStory3");
        assertThat(userStories, containsTestsForStory(userStory1));
        assertThat(userStories, containsTestsForStory(userStory2));
        assertThat(userStories, containsTestsForStory(userStory3));
    }    
    
    
}
