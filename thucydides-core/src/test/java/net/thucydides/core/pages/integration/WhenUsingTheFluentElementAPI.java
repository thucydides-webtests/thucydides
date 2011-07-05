package net.thucydides.core.pages.integration;


import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.junit.rules.SaveWebdriverSystemPropertiesRule;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementFacade;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class WhenUsingTheFluentElementAPI {

    static WebDriver driver;

    @BeforeClass
    public static void initDriver() {
        driver = new WebDriverFacade(FirefoxDriver.class);
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
    }

    @DefaultUrl("classpath:static-site/index.html")
    public class StaticSitePage extends PageObject {

        @FindBy(name="firstname")
        protected WebElement firstName;

        @FindBy(name="lastname")
        protected WebElement lastName;

        @FindBy(name="hiddenfield")
        protected WebElement hiddenField;

        @FindBy(id="color")
        protected WebElement colors;

        @FindBy(name="fieldDoesNotExist")
        protected WebElement fieldDoesNotExist;

        public StaticSitePage(WebDriver driver) {
            super(driver);
        }
    }

    @Test
    public void should_report_if_element_is_visible() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        assertThat(page.element(page.firstName).isVisible(), is(true));
    }

    @Test
    public void should_report_if_element_is_not_visible() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        assertThat(page.element(page.hiddenField).isVisible(), is(false));
    }

    @Test
    public void should_report_element_as_not_visible_if_not_present() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        assertThat(page.element(page.fieldDoesNotExist).isVisible(), is(false));
    }

    @Test(timeout = 1000)
    public void should_report_element_as_not_visible_quickly_if_not_present_right_now() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        assertThat(page.element(page.fieldDoesNotExist).isCurrentlyVisible(), is(false));
    }

    @Test(timeout = 1000, expected = AssertionError.class)
    public void should_check_element_as_not_visible_quickly_if_not_present_right_now() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.fieldDoesNotExist).shouldBeCurrentlyVisible();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_expection_if_required_element_is_not_present() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.fieldDoesNotExist).shouldBeVisible();
    }

    @Test(expected = AssertionError.class)
    public void should_throw_expection_if_unrequired_element_is_present() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.firstName).shouldNotBeVisible();
    }

    @Test(timeout = 1000,expected = AssertionError.class)
    public void should_throw_expection_fast_if_unrequired_element_is_present() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.firstName).shouldNotBeCurrentlyVisible();
    }


    @Test(expected = AssertionError.class)
    public void should_throw_expection_if_required_element_is_not_visible() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.hiddenField).shouldBeVisible();
    }

    @Test
    public void should_pass_if_unwanted_element_is_not_visible() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.hiddenField).shouldNotBeVisible();
    }

    @Test
    public void should_pass_if_unwanted_element_is_not_on_page() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.fieldDoesNotExist).shouldNotBeVisible();
    }

    @Test
    public void should_contain_text_passes_if_field_contains_text() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.colors).shouldContainText("Red");
    }

    @Test(expected = AssertionError.class)
    public void should_contain_text_throws_exception_if_field_does_not_contain_text() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.colors).shouldContainText("Magenta");
    }

    @Test(expected = NoSuchElementException.class)
    public void should_contain_text_throws_exception_if_element_does_not_exist() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.fieldDoesNotExist).shouldContainText("Magenta");
    }

    @Test
    public void should_not_contain_text_passes_if_field_does_not_contains_text() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.colors).shouldNotContainText("Beans");
    }

    @Test(expected = NoSuchElementException.class)
    public void should_not_contain_text_throws_exception_if_field_is_not_found() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.fieldDoesNotExist).shouldNotContainText("Beans");
    }


    @Test(expected = AssertionError.class)
    public void should_not_contain_text_throws_exception_if_field_does_contains_text() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        page.element(page.colors).shouldNotContainText("Red");
    }

    @Test
    public void should_detect_focus_on_input_fields() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_detect_lack_of_focus_on_input_fields() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        assertThat(page.element(page.firstName).hasFocus(), is(false));
    }

}
