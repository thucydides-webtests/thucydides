package net.thucydides.core.model;

import net.thucydides.core.screenshots.ScreenshotAndHtmlSource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class WhenFormattingScreenshotDetailsForHtml {

    @Test
    public void quotes_should_be_presented_as_entities() {
        Screenshot screenshot = new Screenshot("file.png", "Login with user \"bill\"",1000);
        assertThat(screenshot.getHtml().getDescription(), is("Login with user &quot;bill&quot;"));
    }

    @Test
    public void non_ascii_chars_should_be_excluded() {
        Screenshot screenshot = new Screenshot("file.png", "Login with user \"bill\"",1000);
        assertThat(screenshot.getHtml().getDescription(), is("Login with user &quot;bill&quot;"));
    }


}
