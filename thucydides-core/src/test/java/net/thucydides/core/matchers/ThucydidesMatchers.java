package net.thucydides.core.matchers;

import net.thucydides.core.model.Screenshot;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.features.ApplicationFeature;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.List;


public class ThucydidesMatchers {

    @Factory
    public static Matcher<List<Screenshot>> hasFilenames(String... screenshots) {
       return new ScreenshotHasFilenamesMatcher(screenshots);
    }
}
