package net.thucydides.core.images;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class WhenResizingAScreenshot {

    @Test
    public void should_be_able_to_determine_the_size_of_an_image() throws IOException {
        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");
        SimpleImageInfo info = new SimpleImageInfo(screenshotFile);
        assertThat(info.getHeight(), is(788));
        assertThat(info.getWidth(), is(1200));
    }


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
    public void should_not_rescale_if_target_height_is_equal_to_image_height() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(788);

        assertThat(resizedImage, is(image));
    }

    @Test
    public void should_not_try_to_redimension_images_that_are_too_large() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/wikipedia.png");

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(1200);

        assertThat(resizedImage.getWitdh(), is(805));
        assertThat(resizedImage.getHeight(), is(greaterThan(1200)));
    }

    @Test
    public void should_not_try_to_redimension_images_on_small_canvas() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/wikipedia.png");

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(1200);

        assertThat(resizedImage.getWitdh(), is(805));
        assertThat(resizedImage.getHeight(), is(greaterThan(1200)));
    }

    @Test
    public void should_not_try_to_redimension_images_larger_than_the_specified_size() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/wikipedia.png");

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(4000);

        assertThat(resizedImage.getWitdh(), is(805));
        assertThat(resizedImage.getHeight(), is(greaterThan(4000)));
    }



    @Test
    public void should_not_try_to_redimension_images_that_are_higher_than_the_requested_height() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);

        ResizableImage resizedImage = image.rescaleCanvas(400);

        assertThat(resizedImage.getHeight(), is(greaterThan(400)));
    }


    @Test
    public void should_be_able_to_redimension_an_image_by_reducing_its_size() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        int newHeight = 938;

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);
        ResizableImage resizedImage = image.rescaleCanvas(newHeight);

        assertThat(resizedImage.getHeight(), is(newHeight));
    }

    @Test
    public void should_be_able_to_redimension_an_image_by_filling_out_the_background() throws IOException {

        File screenshotFile = screenshotFileFrom("/screenshots/google_page_1.png");

        int newHeight = 1250;

        ResizableImage image = ResizableImage.loadFrom(screenshotFile);
        ResizableImage resizedImage = image.rescaleCanvas(newHeight);

        assertThat(resizedImage.getHeight(), is(newHeight));
    }

    private File screenshotFileFrom(final String screenshot) {
        URL sourcePath = getClass().getResource(screenshot);
        return new File(sourcePath.getPath());
    }
}
