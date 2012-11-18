package net.thucydides.core.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
