package net.thucydides.core.pages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class WhenManinpulatingWebElements {

    @Mock
    WebDriver driver;

    @Mock
    WebElement webElement;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void stale_element_should_not_be_considered_visible() {
        when(webElement.isDisplayed()).thenThrow(new StaleElementReferenceException("Stale element"));

        WebElementFacade elementFacade = new WebElementFacade(driver, webElement, 100);

        assertThat(elementFacade.isVisible(), is(false));

    }

    @Test
    public void stale_element_found_using_a_finder_should_not_be_considered_displayed() {
        when(driver.findElement((By) anyObject())).thenThrow(new StaleElementReferenceException("Stale element"));

        RenderedPageObjectView view = new RenderedPageObjectView(driver, 100);

        assertThat(view.elementIsDisplayed(By.id("some-element")), is(false));

    }


    @Test
    public void inexistant_element_should_not_be_considered_present() {
        when(driver.findElement((By) anyObject())).thenThrow(new NoSuchElementException("It ain't there."));

        RenderedPageObjectView view = new RenderedPageObjectView(driver, 100);

        assertThat(view.elementIsPresent(By.id("some-element")), is(false));

    }

    @Test
    public void inexistant_element_should_not_be_considered_displayed() {
        when(driver.findElement((By) anyObject())).thenThrow(new NoSuchElementException("It ain't there."));

        RenderedPageObjectView view = new RenderedPageObjectView(driver, 100);

        assertThat(view.elementIsDisplayed(By.id("some-element")), is(false));

    }

    @Test
    public void stale_element_should_not_be_considered_enabled() {
        when(webElement.isDisplayed()).thenThrow(new StaleElementReferenceException("Stale element"));

        WebElementFacade elementFacade = new WebElementFacade(driver, webElement, 100);

        assertThat(elementFacade.isCurrentlyEnabled(), is(false));

    }

    @Mock
    JavaScriptExecutorFacade mockJavaScriptExecutorFacade;

    @Test
    public void element_can_set_window_focus() {
        WebElementFacade elementFacade = new WebElementFacade(driver, webElement, 100) {
            @Override
            protected JavaScriptExecutorFacade getJavaScriptExecutorFacade() {
                return mockJavaScriptExecutorFacade;
            }
        };
        elementFacade.setWindowFocus();

        verify(mockJavaScriptExecutorFacade).executeScript("window.focus()");

    }

}
