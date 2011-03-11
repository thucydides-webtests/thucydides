package net.thucydides.core.hamcrest;

import java.util.List;

import net.thucydides.core.model.UserStory;
import net.thucydides.core.model.UserStoryTestResults;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class UserStoryMatchers {

    @Factory
    public static Matcher<List<UserStoryTestResults>> containsTestsForStory(UserStory expectedStory ) {
        return new ContainsUserStoryMatcher(expectedStory);
    }
}
