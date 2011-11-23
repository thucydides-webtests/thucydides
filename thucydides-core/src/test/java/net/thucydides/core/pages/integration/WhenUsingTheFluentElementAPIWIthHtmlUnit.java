package net.thucydides.core.pages.integration;


import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementFacade;
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
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheFluentElementAPIWIthHtmlUnit extends FluentElementAPITestsBaseClass {


    @BeforeClass
    public static void initDriver() {
        driver = new WebDriverFacade(HtmlUnitDriver.class, new WebDriverFactory());
        page = new StaticSitePage(driver, 1);
    }

    @Before
    public void openStaticPage() {
        page.setWaitForTimeout(5000);
        page.open();
    }

    @Test
    public void should_report_if_element_is_visible() {
        assertThat(page.element(page.firstName).isVisible(), is(true));
    }

    @Test
    public void should_report_if_element_is_not_visible() {
        assertThat(page.element(page.hiddenField).isVisible(), is(false));
    }

    @Test
    public void should_report_if_element_is_present() {
        assertThat(page.element(page.firstName).isPresent(), is(true));
    }

    @Test
    public void should_report_if_element_is_present_but_not_visible() {
        assertThat(page.element(page.hiddenField).isPresent(), is(true));
    }

    @Test
    public void should_report_if_element_is_not_present() {
        assertThat(page.element(page.fieldDoesNotExist).isPresent(), is(false));
    }

    @Test
     public void should_pass_if_expected_element_is_present() {
         page.element(page.firstName).shouldBePresent();
     }

     @Test
     public void should_pass_if_expected__if_element_is_present_but_not_visible() {
         page.element(page.hiddenField).shouldBePresent();
     }


     @Test(expected = AssertionError.class)
     public void should_throw_exception_if_element_is_not_present() {
         page.fieldDoesNotExistShouldBePresent();
     }

    @Test
    public void should_pass_if_unexpected_element_is_not_present() {
        page.fieldDoesNotExistShouldNotBePresent();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_exception_if_unexpected_element_is_present() {
        page.hiddenFieldShouldNotBePresent();
    }

    @Test
    public void should_wait_for_hidden_elements() {
        page.waitForRenderedElementsToBePresent(By.name("city"));
    }

    @Test
    public void wait_for_hidden_elements_should_work_for_visible_elements() {
        page.waitForRenderedElementsToBePresent(By.name("firstname"));
    }

    @Test(expected = TimeoutException.class)
    public void wait_for_hidden_elements_should_fail_for_missing_elements() {
        page.waitForRenderedElementsToBePresent(By.name("noSuchField"));
    }

    @Test
    public void should_report_element_as_not_visible_if_not_present() {
        assertThat(page.element(page.fieldDoesNotExist).isVisible(), is(false));
    }

    @Test(timeout = 1000)
    public void should_report_element_as_not_visible_quickly_if_not_present_right_now() {
        assertThat(page.element(page.fieldDoesNotExist).isCurrentlyVisible(), is(false));
    }

    @Test(timeout = 1000, expected = AssertionError.class)
    public void should_check_element_as_not_visible_quickly_if_not_present_right_now() {
        page.element(page.fieldDoesNotExist).shouldBeCurrentlyVisible();
    }

    @Test
    public void should_check_element_as_visible_quickly_if_not_present_right_now() {
        page.element(page.firstName).shouldBeCurrentlyVisible();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_expection_if_required_element_is_not_present() {
        page.element(page.fieldDoesNotExist).shouldBeVisible();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_expection_if_unrequired_element_is_present() {
        page.element(page.firstName).shouldNotBeVisible();
    }

    @Test(timeout = 1000,expected = AssertionError.class)
    public void should_throw_expection_fast_if_unrequired_element_is_present() {
        page.element(page.firstName).shouldNotBeCurrentlyVisible();
    }

    @Test
    public void should_check_element_as_invisible_quickly_if_present_right_now() {
        page.element(page.hiddenField).shouldNotBeCurrentlyVisible();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_expection_if_required_element_is_not_visible() {
        page.element(page.hiddenField).shouldBeVisible();
    }

    @Test
    public void should_know_if_enabled_element_is_enabled() {
        assertThat(page.element(page.firstName).isEnabled(), is(true));
    }

    @Test
    public void should_be_able_to_chain_methods() {
        page.element(page.buttonThatIsInitiallyDisabled).waitUntilEnabled().and().then().click();
    }

    @Test
    public void should_know_if_disabled_element_is_not_enabled() {
        assertThat(page.element(page.readonlyField).isEnabled(), is(false));
    }

    @Test
    public void should_do_nothing_if_enabled_field_should_be_enabled() {
        page.element(page.firstName).shouldBeEnabled();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_exception_if_enabled_field_should_be_disabled() {
        page.element(page.firstName).shouldNotBeEnabled();
    }

    @Test
    public void should_work_if_disabled_field_is_bot_enabled() {
        page.element(page.readonlyField).shouldNotBeEnabled();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_exception_if_disabled_field_should_be_enabled() {
        page.element(page.readonlyField).shouldBeEnabled();
    }

    @Test
    public void should_pass_if_unwanted_element_is_not_visible() {
        page.element(page.hiddenField).shouldNotBeVisible();
    }

    @Test
    public void should_pass_if_unwanted_element_is_not_on_page() {
        page.element(page.fieldDoesNotExist).shouldNotBeVisible();
    }

}
