package net.thucydides.core.screenshots;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

public class WhenUsingRecordedScreenshots {

    @Test
    public void a_screenshot_was_taken_if_the_screenshot_file_is_not_null() {
        RecordedScreenshot recordedScreenshot = new RecordedScreenshot(new File("screen.png"), new File("screen.html"));
        assertThat(recordedScreenshot.wasTaken(), is(true));
    }

    @Test
    public void a_screenshot_was_not_taken_if_the_screenshot_file_is_null() {
        RecordedScreenshot recordedScreenshot = new RecordedScreenshot(null, null);
        assertThat(recordedScreenshot.wasTaken(), is(false));
    }

    @Test
    public void a_screenshot_is_equal_to_itself() {
        RecordedScreenshot recordedScreenshot = new RecordedScreenshot(screenshotFileFrom("/screenshots/amazon.png"), new File("screen.html"));
        assertThat(recordedScreenshot, is(recordedScreenshot));
    }

    @Test
    public void a_screenshot_is_not_equal_to_an_object_of_a_different_type() {
        RecordedScreenshot recordedScreenshot = new RecordedScreenshot(screenshotFileFrom("/screenshots/amazon.png"), new File("screen.html"));
        assertThat(recordedScreenshot.equals(screenshotFileFrom("/screenshots/amazon.png")), is(false));
    }

    @Test
    public void screenshots_with_the_same_image_are_considered_identical() {
        RecordedScreenshot recordedScreenshot = new RecordedScreenshot(screenshotFileFrom("/screenshots/amazon.png"), new File("screen.html"));
        RecordedScreenshot identicalScreenshot = new RecordedScreenshot(screenshotFileFrom("/screenshots/amazon.png"), new File("screen.html"));
        
        assertThat(recordedScreenshot, is(identicalScreenshot));
        assertThat(recordedScreenshot.hashCode(), is(identicalScreenshot.hashCode()));
    }

    @Test
    public void screenshots_with_different_images_are_considered_unidentical() {
        RecordedScreenshot recordedScreenshot = new RecordedScreenshot(screenshotFileFrom("/screenshots/google_page_1.png"), new File("screen.html"));
        RecordedScreenshot differentScreenshot = new RecordedScreenshot(screenshotFileFrom("/screenshots/google_page_2.png"), new File("screen.html"));

        assertThat(recordedScreenshot, is(not(differentScreenshot)));
        assertThat(recordedScreenshot.hashCode(), is(not(differentScreenshot.hashCode())));
    }

    @Test
    public void a_screenshot_with_an_image_is_not_equal_to_a_null_screenshot() {
        RecordedScreenshot recordedScreenshot = new RecordedScreenshot(screenshotFileFrom("/screenshots/google_page_1.png"), new File("screen.html"));
        RecordedScreenshot nullScreenshot = new RecordedScreenshot(null, null);

        assertThat(recordedScreenshot, is(not(nullScreenshot)));
        assertThat(nullScreenshot, is(not(recordedScreenshot)));
    }

    private File screenshotFileFrom(final String screenshot) {
        URL sourcePath = getClass().getResource(screenshot);
        return new File(sourcePath.getPath());
    }

}
