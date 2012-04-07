package net.thucydides.core.matchers;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.Screenshot;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.features.ApplicationFeature;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.File;
import java.util.List;


public class FileMatchers {

    @Factory
    public static Matcher<File> exists() {
        return new TypeSafeMatcher<File>() {
            private File checkedFile;

            @Override
            public boolean matchesSafely(File file) {
                checkedFile = file;
                return file.exists();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a file at " + checkedFile.getPath());
            }
        };
    }
}
