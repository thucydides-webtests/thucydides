package net.thucydides.core.hamcrest;

import java.util.List;

import net.thucydides.core.model.UserStory;
import net.thucydides.core.model.UserStoryTestResults;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ContainsUserStoryMatcher extends TypeSafeMatcher<List<UserStoryTestResults>> {
    
    private UserStory userStory;
    
    public ContainsUserStoryMatcher(UserStory userStory) {
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
