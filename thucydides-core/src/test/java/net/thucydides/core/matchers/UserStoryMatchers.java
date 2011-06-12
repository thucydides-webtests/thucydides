package net.thucydides.core.matchers;

import net.thucydides.core.model.Story;
import net.thucydides.core.model.UserStoryTestResults;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.List;

public class UserStoryMatchers {

    @Factory
    public static Matcher<List<UserStoryTestResults>> containsTestsForStory(Story expectedStory ) {
        return new ContainsUserStoryMatcher(expectedStory);
    }
}
