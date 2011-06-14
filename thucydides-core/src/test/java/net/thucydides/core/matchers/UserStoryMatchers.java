package net.thucydides.core.matchers;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.features.ApplicationFeature;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.List;

public class UserStoryMatchers {

    @Factory
    public static Matcher<List<StoryTestResults>> containsTestsForStory(Story expectedStory ) {
        return new ContainsUserStoryMatcher(expectedStory);
    }

    @Factory
    public static Matcher<List<FeatureResults>> containsApplicationFeature(ApplicationFeature feature ) {
        return new ContainsFeatureMatcher(feature);
    }


}
