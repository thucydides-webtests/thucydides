package net.thucydides.core.matchers;

import net.thucydides.core.model.UserStory;
import net.thucydides.core.model.UserStoryTestResults;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.List;

public class UserStoryMatchers {

    @Factory
    public static Matcher<List<UserStoryTestResults>> containsTestsForStory(UserStory expectedStory ) {
        return new ContainsUserStoryMatcher(expectedStory);
    }
}
