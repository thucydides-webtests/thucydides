package net.thucydides.junit.integration.samples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import net.thucydides.junit.annotations.Managed;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * This is a very simple scenario of testing a single page.
 * @author johnsmart
 *
 */
@RunWith(ThucydidesRunner.class)
public class ManagedWebDriverSampleWithAFailingTest {

    @Managed
    public WebDriver driver;

    @Test
    public void the_user_opens_the_page() {
        driver.get("http://www.google.com");       
    }
    
    @Test
    public void the_user_performs_a_search_on_cats() {
        assertThat(1, is(2));
    }
    
    @Test
    public void the_results_page_title_should_contain_the_word_Cats() {
        assertThat(driver.getTitle(), containsString("cats"));
    }
}
