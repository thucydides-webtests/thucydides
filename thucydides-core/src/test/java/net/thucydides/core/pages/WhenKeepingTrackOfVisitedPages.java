package net.thucydides.core.pages;


import static org.mockito.Mockito.verify;

import net.thucydides.core.annotations.At;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

import org.openqa.selenium.WebDriver;

import java.net.URL;

public class WhenKeepingTrackOfVisitedPages {

    @Mock
    WebDriver driver;
    
    @Rule
    public MethodRule saveSystemProperties = new SaveWebdriverSystemPropertiesRule();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void the_pages_object_should_have_a_default_starting_point_url() {

        final String baseUrl = "http://www.google.com";
        final Pages pages = new Pages(driver);
        pages.setDefaultBaseUrl(baseUrl);

        pages.start();
        
        verify(driver).get(baseUrl);    
    }


    @Test
    public void the_default_starting_point_url_can_refer_to_a_file_on_the_classpath() {

        final String baseUrl = "classpath:static-site/index.html";
        final Pages pages = new Pages(driver);
        pages.setDefaultBaseUrl(baseUrl);

        URL staticSiteUrl = Thread.currentThread().getContextClassLoader().getResource("static-site/index.html");

        pages.start();

        verify(driver).get(staticSiteUrl.toString());
    }

    @Test
    public void the_default_starting_point_url_can_be_overriden_by_a_system_property() {

        final String defaultBaseUrl = "http://www.google.com";
        final String systemDefinedBaseUrl = "http://www.google.com.au";
        final Pages pages = new Pages(driver);
        
        System.setProperty("webdriver.base.url", systemDefinedBaseUrl);
        
        pages.start();
        
        verify(driver).get(systemDefinedBaseUrl);    
    }

    @Test
    public void the_pages_object_knows_when_we_are_on_the_right_page() {

        when(driver.getCurrentUrl()).thenReturn("http://www.apache.org");
        final Pages pages = new Pages(driver);
        pages.start();

        assertThat(pages.isCurrentPageAt(ApacheHomePage.class), is(true));
    }

    @Test
    public void the_pages_object_knows_when_we_are_not_on_the_right_page() {

        when(driver.getCurrentUrl()).thenReturn("http://www.google.org");
        final Pages pages = new Pages(driver);
        pages.start();

        assertThat(pages.isCurrentPageAt(ApacheHomePage.class), is(false));
    }

    @Test(expected = WrongPageError.class)
    public void the_pages_object_throws_a_wrong_page_error_when_we_expect_the_wrong_page() {

        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");
        final Pages pages = new Pages(driver);
        pages.start();

        pages.currentPageAt(ApacheHomePage.class);
    }


    public final class InvalidHomePage extends PageObject {
        public InvalidHomePage() {
            super(null);
        }
    }

    @Test(expected = WrongPageError.class)
    public void the_pages_object_throws_a_wrong_page_error_when_the_page_object_is_invalid() {

        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");
        final Pages pages = new Pages(driver);
        pages.start();

        pages.currentPageAt(InvalidHomePage.class);
    }

    public final class ExplodingHomePage extends PageObject {
        public ExplodingHomePage(final WebDriver driver) throws InstantiationException {
            super(null);
            throw new InstantiationException();
        }
    }

    @Test(expected = WrongPageError.class)
    public void the_pages_object_throws_a_wrong_page_error_when_the_page_object_cant_be_instanciated() {

        when(driver.getCurrentUrl()).thenReturn("http://www.google.com");
        final Pages pages = new Pages(driver);
        pages.start();

        pages.currentPageAt(ExplodingHomePage.class);
    }
}
