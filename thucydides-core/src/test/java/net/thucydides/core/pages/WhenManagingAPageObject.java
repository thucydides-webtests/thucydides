package net.thucydides.core.pages;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenManagingAPageObject {

    @Mock
    WebDriver driver;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    final class BasicPageObject extends PageObject {
        public BasicPageObject(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void the_page_gets_the_title_from_the_web_page() {

        when(driver.getTitle()).thenReturn("Google Search Page");
        BasicPageObject page = new BasicPageObject(driver);
        
        assertThat(page.getTitle(), is("Google Search Page"));
        
    }
}
