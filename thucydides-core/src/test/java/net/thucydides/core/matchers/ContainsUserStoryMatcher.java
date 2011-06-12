package net.thucydides.core.matchers;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.UserStoryTestResults;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

public class ContainsUserStoryMatcher extends TypeSafeMatcher<List<UserStoryTestResults>> {
    
    private Story userStory;
    
    public ContainsUserStoryMatcher(Story userStory) {
        this.userStory = userStory;
    }

    public boolean matchesSafely(List<UserStoryTestResults> userStories) {        
        for(UserStoryTestResults userStoryResults : userStories) {
            if (userStoryResults.containsResultsFor(userStory)) {
                return true;
            }
        }
        return false;
    }


    public void describeTo(Description description) {
        description.appendText("a collection of user stories containing ").appendText(userStory.toString());
    }
}
