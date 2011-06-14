package net.thucydides.core.matchers;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.StoryTestResults;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

public class ContainsUserStoryMatcher extends TypeSafeMatcher<List<StoryTestResults>> {
    
    private Story userStory;
    
    public ContainsUserStoryMatcher(Story userStory) {
        this.userStory = userStory;
    }

    public boolean matchesSafely(List<StoryTestResults> stories) {
        for(StoryTestResults storyResults : stories) {
            if (storyResults.containsResultsFor(userStory)) {
                return true;
            }
        }
        return false;
    }


    public void describeTo(Description description) {
        description.appendText("a collection of user stories containing ").appendText(userStory.toString());
    }
}
