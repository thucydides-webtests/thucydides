package net.thucydides.core.images;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenResizingAScreenshot {

    @Test
    public void should_be_able_to_determine_the_dimensions_of_an_image() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        int expectedWidth = 1200;
        int expectedHeight = 788;

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        assertThat(image.getWitdh(), is(expectedWidth));
        assertThat(image.getHeight(), is(expectedHeight));
    }

    @Test
    public void should_be_able_to_determine_the_maximum_dimensions_from_a_set_of_screenshots() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        int expectedWidth = 1200;
        int expectedHeight = 788;

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        assertThat(image.getWitdh(), is(expectedWidth));
        assertThat(image.getHeight(), is(expectedHeight));
    }


    @Test
    public void should_be_able_to_redimension_an_image_by_filling_out_the_background() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        int newWidth = 1200;
        int newHeight = 1250;

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(newWidth, newHeight);

        assertThat(resizedImage.getWitdh(), is(newWidth));
        assertThat(resizedImage.getHeight(), is(newHeight));
    }

    private File screenshotFileFrom(final String screenshot) {
        URL sourcePath = getClass().getResource(screenshot);
        return new File(sourcePath.getPath());
    }
}
