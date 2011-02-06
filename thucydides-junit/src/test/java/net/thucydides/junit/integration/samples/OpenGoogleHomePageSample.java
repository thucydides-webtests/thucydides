package net.thucydides.junit.integration.samples;

import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * This is a very simple scenario of testing a single page.
 * @author johnsmart
 *
 */
@RunWith(ThucydidesRunner.class)
public class OpenGoogleHomePageSample {

    @Managed
    public WebDriver driver;

    @Test
    public void the_user_opens_the_page() {
        driver.get("http://www.google.com");       
    }    
}
