package net.thucydides.core.pages.integration;


import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webdriver.jquery.ByJQuery;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.WebDriverFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheFluentElementAPIToWaitForElements  extends FluentElementAPITestsBaseClass {

    @Test
    public void should_wait_for_field_to_appear_before_entering_data() {
        assertThat(page.element(page.city).isCurrentlyVisible(), is(false));
        page.element(page.city).type("Denver");

        assertThat(page.element(page.city).isCurrentlyVisible(), is(true));
        assertThat(page.city.getAttribute("value"), is("Denver"));
    }

    @Test
    public void should_wait_for_field_to_appear() {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.open();

        assertThat(page.element(page.city).isCurrentlyVisible(), is(false));
        page.element(page.city).waitUntilVisible();

        assertThat(page.element(page.city).isCurrentlyVisible(), is(true));
    }

    @Test
    public void should_succeed_if_waiting_for_an_existing_field_to_appear() {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.open();

        assertThat(page.element(page.country).isCurrentlyVisible(), is(true));
        page.element(page.country).waitUntilVisible();
    }

    @Test
    public void should_wait_for_field_to_be_enabled() throws InterruptedException {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.open();

        assertThat(page.element(page.buttonThatIsInitiallyDisabled).isCurrentlyEnabled(), is(false));

        page.element(page.buttonThatIsInitiallyDisabled).waitUntilEnabled();

        assertThat(page.element(page.buttonThatIsInitiallyDisabled).isCurrentlyEnabled(), is(true));
    }

    @Test
    public void should_wait_for_field_to_be_enabled_using_alternative_style() throws InterruptedException {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.open();

        page.firstName().waitUntilVisible();
        page.firstName().waitUntilEnabled();
    }


    @Test(expected = ElementNotVisibleException.class)
    public void should_fail_if_wait_for_field_to_be_enabled_never_happens() throws InterruptedException {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.setWaitForTimeout(100);
        page.open();

        page.element(page.readonlyField).waitUntilEnabled();
    }

    @Test(expected = ElementNotVisibleException.class)
    public void should_fail_if_wait_for_field_to_be_disabled_never_happens() throws InterruptedException {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.setWaitForTimeout(100);
        page.open();

        page.element(page.firstName).waitUntilDisabled();
    }

    @Test
    public void should_pass_immediately_if_wait_for_field_to_be_disabled_is_already_disabled() throws InterruptedException {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.setWaitForTimeout(100);
        page.open();

        page.element(page.readonlyField).waitUntilDisabled();
    }

    @Test
    public void is_currently_enabled_should_be_false_for_an_inexistant_element() throws InterruptedException {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.setWaitForTimeout(100);
        page.open();

        assertThat(page.element(page.fieldDoesNotExist).isCurrentlyEnabled(), is(false));
    }

    @Test
    public void should_wait_for_field_to_be_disabled() throws InterruptedException {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.open();

        assertThat(page.element(page.buttonThatIsInitiallyEnabled).isCurrentlyEnabled(), is(true));

        page.element(page.buttonThatIsInitiallyEnabled).waitUntilDisabled();

        assertThat(page.element(page.buttonThatIsInitiallyEnabled).isCurrentlyEnabled(), is(false));
    }

    @Test(expected = ElementNotVisibleException.class)
    public void should_throw_expection_if_waiting_for_field_that_does_not_appear() {
        page.setWaitForTimeout(100);
        page.open();

        assertThat(page.element(page.hiddenField).isCurrentlyVisible(), is(false));

        page.element(page.hiddenField).waitUntilVisible();
    }

    @Test
    public void should_pass_immediately_if_waiting_for_field_that_is_present() {
        page.setWaitForTimeout(100);
        page.open();

        page.element(page.firstName).waitUntilVisible();
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_wait_for_field_to_disappear() {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.open();

        assertThat(page.element(page.placetitle).isCurrentlyVisible(), is(true));
        page.element(page.placetitle).waitUntilNotVisible();

        assertThat(page.element(page.placetitle).isCurrentlyVisible(), is(false));
    }

    @Test(expected = ElementNotVisibleException.class)
    public void should_throw_exception_if_waiting_for_field_tbat_does_not_disappear() {
        page.setWaitForTimeout(100);
        page.open();

        assertThat(page.element(page.firstName).isCurrentlyVisible(), is(true));
        page.element(page.firstName).waitUntilNotVisible();
    }

    @Test
    public void should_wait_for_text_to_dissapear() {
        assertThat(page.containsText("Dissapearing text"), is(true));

        page.setWaitForTimeout(5000);
        page.waitForTextToDisappear("Dissapearing text");

        assertThat(page.containsText("Dissapearing text"), is(false));
    }

    @Test
    public void should_wait_for_text_in_element_to_dissapear() {
        assertThat(page.containsText("Dissapearing text"), is(true));

        page.setWaitForTimeout(5000);
        page.waitForTextToDisappear(page.dissapearingtext, "Dissapearing text");

        assertThat(page.containsText("Dissapearing text"), is(false));
    }

    @Test(expected = TimeoutException.class)
    public void should_timeout_when_waiting_for_elements_to_dissapear() {
        page.waitForTextToDisappear("A visible title", 500);
    }

    @Test(expected = TimeoutException.class)
    public void should_timeout_if_wait_for_text_in_element_to_dissapear_fails() {
        page.setWaitForTimeout(200);
        page.waitForTextToDisappear(page.colors, "Red");
    }

    @Test
    public void should_wait_for_elements_to_appear() {
        assertThat(page.element(page.city).isCurrentlyVisible(), is(false));

        page.waitForAnyRenderedElementOf(By.id("city"));

        assertThat(page.element(page.city).isCurrentlyVisible(), is(true));
    }

    @Test
    public void should_display_meaningful_error_messages_in_firefox_if_waiting_for_field_that_does_not_appear() {
        StaticSitePage page = new StaticSitePage(driver, 1);

        expectedException.expect(ElementNotVisibleException.class);
        expectedException.expectMessage(allOf(containsString("Unable to locate element"),
                containsString("fieldDoesNotExist")));

        page.setWaitForTimeout(200);
        page.open();

        page.element(page.fieldDoesNotExist).waitUntilVisible();
    }



}
