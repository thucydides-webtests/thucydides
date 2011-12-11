package net.thucydides.core.pages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
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

    class PageObjectWithElement extends PageObject {

        public WebElement webElement = null;

        public PageObjectWithElement(final WebDriver driver) {
            super(driver);
        }
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
    public void timeout_can_be_redefined() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, webElement, 100);
        WebElementFacade webElementFacadeWithDifferentTimeout = webElementFacade.withTimeoutOf(2, TimeUnit.SECONDS);

        assertThat(webElementFacadeWithDifferentTimeout.getTimeoutInMilliseconds(), is(2000L));
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

    @Test
    public void when_text_attribute_is_null_textvalue_should_return_value() {
        when(webElement.isDisplayed()).thenReturn(true);
        when(webElement.getText()).thenReturn(null);
        when(webElement.getAttribute("value")).thenReturn("value");

        WebElementFacade elementFacade = new WebElementFacade(driver, webElement, 100);

        assertThat(elementFacade.getTextValue(), is("value"));
    }

    @Test
    public void when_text_attribute_and_text_value_are_null_textvalue_should_return_empty_string() {
        when(webElement.isDisplayed()).thenReturn(true);
        when(webElement.getText()).thenReturn(null);
        when(webElement.getAttribute("value")).thenReturn(null);

        WebElementFacade elementFacade = new WebElementFacade(driver, webElement, 100);

        assertThat(elementFacade.getTextValue(), is(""));
    }

    @Test
    public void when_value_is_null_textvalue_should_return_text() {
        when(webElement.isDisplayed()).thenReturn(true);
        when(webElement.getText()).thenReturn("text");
        when(webElement.getAttribute("value")).thenReturn(null);

        WebElementFacade elementFacade = new WebElementFacade(driver, webElement, 100);

        assertThat(elementFacade.getTextValue(), is("text"));
    }

    @Test
    public void when_webelement_is_null_it_should_be_considered_invisible() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);

        assertThat(webElementFacade.isVisible(), is(false));
    }

    @Test
    public void when_webelement_is_null_it_should_be_considered_not_present() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);

        assertThat(webElementFacade.isPresent(), is(false));
    }

    @Test
    public void when_webelement_is_null_it_should_be_considered_not_enabled() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        assertThat(webElementFacade.isEnabled(), is(false));
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_it_should_not_be_clickable() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        webElementFacade.click();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_it_should_fail_wait_until_enabled() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        webElementFacade.waitUntilEnabled();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_it_should_fail_wait_until_disabled() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        webElementFacade.waitUntilDisabled();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_it_should_fail_wait_until_present() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        webElementFacade.waitUntilPresent();
    }

    @Test
    public void when_webelement_is_null_it_should_succesd_wait_until_not_visible() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        webElementFacade.waitUntilNotVisible();
    }

    @Test
    public void when_webelement_is_null_contains_text_should_fail() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        assertThat(webElementFacade.containsText("text"), is(false));
    }

    @Test
    public void when_webelement_is_null_contains_selected_value_should_fail() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        assertThat(webElementFacade.containsSelectOption("value"), is(false));
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_get_selected_value_should_fail() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        webElementFacade.getSelectedValue();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_get_text_value_should_fail() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        webElementFacade.getTextValue();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_get_value_should_fail() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        webElementFacade.getValue();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void when_webelement_is_null_get_text_should_fail() {
        WebElementFacade webElementFacade = new WebElementFacade(driver, null, 100);
        webElementFacade.getText();
    }
}
