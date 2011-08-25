package net.thucydides.core.pages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
    public void stale_element_should_not_be_considered_enable() {
        when(webElement.isDisplayed()).thenThrow(new StaleElementReferenceException("Stale element"));

        WebElementFacade elementFacade = new WebElementFacade(driver, webElement, 100);

        assertThat(elementFacade.isCurrentlyEnabled(), is(false));

    }

}
