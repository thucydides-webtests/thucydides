package net.thucydides.core.pages.integration;


import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenUsingTheFluentElementAPI {

    static WebDriver driver;

    static StaticSitePage page;

    @BeforeClass
    public static void initDriver() {
        driver = new WebDriverFacade(FirefoxDriver.class);
        page = new StaticSitePage(driver);
        page.setWaitForTimeout(100);
        page.open();
    }

    @AfterClass
    public static void closeBrowser() {
        driver.quit();
    }

    @DefaultUrl("classpath:static-site/index.html")
    public static final class StaticSitePage extends PageObject {

        @FindBy(name="firstname")
        protected WebElement firstName;

        @FindBy(name="lastname")
        protected WebElement lastName;

        @FindBy(name="city")
        protected WebElement city;

        @FindBy(name="hiddenfield")
        protected WebElement hiddenField;

        protected WebElement readonlyField;

        protected WebElement doesNotExist;

        @FindBy(id="color")
        protected WebElement colors;

        protected WebElement elements;

        @FindBy(name="fieldDoesNotExist")
        protected WebElement fieldDoesNotExist;

        public StaticSitePage(WebDriver driver) {
            super(driver);
        }

        public void setFirstName(String value) {
            element(firstName).type(value);
        }

        public void fieldDoesNotExistShouldNotBePresent() {
            element(fieldDoesNotExist).shouldNotBePresent();
        }

        public void fieldDoesNotExistShouldBePresent() {
            element(fieldDoesNotExist).shouldBePresent();
        }
        public void hiddenFieldShouldNotBePresent() {
            element(hiddenField).shouldNotBePresent();
        }
    }

    @Before
    public void openStaticPage() {
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
        page.waitForRenderedElementsToBePresent(By.name("hiddenfield"));
    }

    @Test
    public void wait_for_hidden_elements_should_work_for_visible_elements() {
        page.waitForRenderedElementsToBePresent(By.name("firstname"));
    }

    @Test(expected = ElementNotVisibleException.class)
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


    @Test(expected = AssertionError.class)
    public void should_throw_expection_if_required_element_is_not_visible() {
        page.element(page.hiddenField).shouldBeVisible();
    }

    @Test
    public void should_know_if_enabled_element_is_enabled() {
        assertThat(page.element(page.firstName).isEnabled(), is(true));
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

    @Test
    public void should_contain_text_passes_if_field_contains_text() {
        page.element(page.colors).shouldContainText("Red");
    }

    @Test(expected = AssertionError.class)
    public void should_contain_text_throws_exception_if_field_does_not_contain_text() {
        page.element(page.colors).shouldContainText("Magenta");
    }

    @Test(expected = NoSuchElementException.class)
    public void should_contain_text_throws_exception_if_element_does_not_exist() {
        page.element(page.fieldDoesNotExist).shouldContainText("Magenta");
    }

    @Test
    public void should_not_contain_text_passes_if_field_does_not_contains_text() {
        page.element(page.colors).shouldNotContainText("Beans");
    }

    @Test(expected = NoSuchElementException.class)
    public void should_not_contain_text_throws_exception_if_field_is_not_found() {
        page.element(page.fieldDoesNotExist).shouldNotContainText("Beans");
    }


    @Test(expected = AssertionError.class)
    public void should_not_contain_text_throws_exception_if_field_does_contains_text() {
        page.element(page.colors).shouldNotContainText("Red");
    }

    @Test
    public void should_detect_focus_on_input_fields() {
        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_detect_lack_of_focus_on_input_fields() {
        assertThat(page.element(page.firstName).hasFocus(), is(false));
    }

    @Test
    public void should_evaluate_javascript_within_browser() {
        String result = (String) page.evaluateJavascript("return document.title");
        assertThat(result, is("Thucydides Test Site"));
    }

    @Test
    public void should_execute_javascript_within_browser() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();
        assertThat(page.element(page.firstName).hasFocus(), is(false));
        page.evaluateJavascript("document.getElementById('firstname').focus()");
        assertThat(page.element(page.firstName).hasFocus(), is(true));
    }

    @Test
    public void should_clear_field_before_entering_text() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).type("joe");

        assertThat(page.firstName.getAttribute("value"), is("joe"));
    }

    @Test
    public void should_optionally_type_enter_after_entering_text() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndEnter("joe");

        assertThat(page.firstName.getAttribute("value"), is("joe"));
    }

    @Ignore("WebDriver doesn't like tabs at the moment")
    @Test
    public void should_optionally_type_tab_after_entering_text() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndTab("joe");

        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_wait_for_field_to_appear_before_entering_data() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();

        assertThat(page.element(page.city).isCurrentlyVisible(), is(false));
        page.element(page.city).type("Denver");

        assertThat(page.element(page.city).isCurrentlyVisible(), is(true));
        assertThat(page.city.getAttribute("value"), is("Denver"));
    }

    @Test
    public void should_select_dropdown_by_visible_text() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();

        page.element(page.colors).selectByVisibleText("Blue");
        assertThat(page.element(page.colors).getSelectedVisibleTextValue(), is("Blue"));
    }

    @Test
    public void should_select_dropdown_by_value() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();

        page.element(page.colors).selectByValue("blue");
        assertThat(page.element(page.colors).getSelectedValue(), is("blue"));
    }

    @Test
    public void should_select_dropdown_by_index_value() {
        StaticSitePage page = new StaticSitePage(driver);
        page.open();

        page.element(page.colors).selectByIndex(2);
        assertThat(page.element(page.colors).getSelectedValue(), is("green"));
    }

}
