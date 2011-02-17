package net.thucydides.junit.integration.samples;

import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.annotations.Step;
import net.thucydides.junit.annotations.Title;
import net.thucydides.junit.annotations.StepDescription;
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
@Title("Open the Google home page")
public class OpenGoogleHomePageWithTitleSample {

    @Managed
    public WebDriver driver;

    @Test @Step(1)
    @StepDescription("The user opens the Google home page.")
    public void the_user_opens_the_page() {
        driver.get("http://www.google.com");       
    }    
}
