package net.thucydides.core.pages.integration;


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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class WhenUsingFormsWithTheFluentElementAPI extends FluentElementAPITestsBaseClass {

    @Test
    public void should_detect_focus_on_input_fields() {
        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_detect_focus_on_input_fields_using_page_API() {
        assertThat(page.hasFocus(page.lastName), is(true));
    }

    @Test
    public void should_detect_lack_of_focus_on_input_fields() {
        assertThat(page.element(page.firstName).hasFocus(), is(false));
    }

    @Test
    public void should_obtain_text_value_from_input() {
        assertThat(page.element(page.firstName).getValue(), is("<enter first name>"));
    }

    @Test
    public void should_obtain_text_value_from_text_area() {
        assertThat(page.element(page.textField).getText(), is("text value"));
    }

    @Test
    public void should_clear_field_before_entering_text() {
        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).type("joe");

        assertThat(page.firstName.getAttribute("value"), is("joe"));
    }

    @Test
    public void should_optionally_type_enter_after_entering_text() {
        StaticSitePage page = new StaticSitePage(chromeDriver, 2000);

        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndEnter("joe");

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));
    }

    @Test
    public void should_optionally_type_tab_after_entering_text_on_linux() {

        if (runningOnLinux()) {
            StaticSitePage page = new StaticSitePage(chromeDriver, 2000);

            page.open();

            assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

            page.element(page.firstName).typeAndTab("joe");

            assertThat(page.element(page.lastName).hasFocus(), is(true));
        }
    }

    @Test
    public void should_optionally_type_tab_after_entering_text_in_firefox() {

        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndTab("joe");

        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_trigger_blur_event_when_focus_leavs_field() {

        StaticSitePage page = new StaticSitePage(chromeDriver, 2000);

        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        assertThat(page.focusmessage.getText(), is(""));

        page.element(page.firstName).typeAndTab("joe");

        assertThat(page.focusmessage.getText(), is("focus left firstname"));
    }


    @Test
    public void should_select_dropdown_by_visible_text() {
        page.open();

        page.element(page.colors).selectByVisibleText("Blue");
        assertThat(page.element(page.colors).getSelectedVisibleTextValue(), is("Blue"));
    }

    @Test
    public void should_select_dropdown_by_value() {
        page.open();

        page.element(page.colors).selectByValue("blue");
        assertThat(page.element(page.colors).getSelectedValue(), is("blue"));
    }

    @Test
    public void should_select_dropdown_by_index_value() {
        page.open();

        page.element(page.colors).selectByIndex(2);
        assertThat(page.element(page.colors).getSelectedValue(), is("green"));
    }

    @Test
    public void should_detect_text_contained_in_a_web_element() {
        assertThat(page.element(page.grid).containsText("joe"), is(true));
    }

    @Test
    public void should_detect_dropdown_entry_contained_in_a_web_element() {
        assertThat(page.element(page.grid).containsText("joe"), is(true));
    }

    @Test
    public void should_detect_text_not_contained_in_a_web_element() {
        assertThat(page.element(page.grid).containsText("red"), is(false));
    }

    @Test
    public void should_obtain_text_value_from_text_area_using_getTextValue() {
        assertThat(page.element(page.textField).getTextValue(), is("text value"));
    }

    @Test
    public void should_obtain_text_value_from_input_using_getTextValue() {
        assertThat(page.element(page.firstName).getTextValue(), is("<enter first name>"));
    }

    @Test
    public void should_return_empty_string_from_other_element_using_getTextValue() {
        assertThat(page.element(page.emptylist).getTextValue(), is(""));
    }

    @Test
    public void should_wait_for_element_to_be_visible_and_enabled_before_clicking() {
        page.element(page.checkbox).click();

    }


    @Test
    public void should_be_able_to_build_composite_wait_until_enabled_clauses() throws InterruptedException {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.open();

        page.waitForCondition().until(page.firstAndLastNameAreEnabled());
    }

    @Test
    public void should_be_able_to_build_composite_wait_until_disabled_clauses() throws InterruptedException {
        StaticSitePage page = new StaticSitePage(driver, 2000);
        page.open();

        page.waitForCondition().until(page.twoFieldsAreDisabled());
    }


    @Test
    public void should_detect_when_a_checkbox_is_selected() {
        assertThat(page.element(page.selectedCheckbox).isSelected(), is(true));
    }

    @Test
    public void should_detect_when_a_checkbox_is_not_selected() {

        assertThat(page.element(page.checkbox).isSelected(), is(false));
    }

    @Test
    public void should_detect_when_a_radio_button_is_selected() {
        assertThat(page.element(page.radioButton1).isSelected(), is(true));
    }

    @Test
    public void should_detect_when_a_radio_button_is_not_selected() {

        assertThat(page.element(page.radioButton2).isSelected(), is(false));
    }

    @Test(timeout = 1000)
    public void should_report_element_as_not_currently_visible_if_field_is_hidden_using_css_display_none() {
        assertThat(page.element(page.csshiddenfield).isCurrentlyVisible(), is(false));
    }


    @Test
    public void should_be_able_to_clear_a_text_field_using_deletes() {
        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).clear();

        assertThat(page.firstName.getAttribute("value"), is(""));
    }

    @Test
    public void should_return_empty_string_when_a_tag_does_not_have_any_text() {
        assertThat(page.element(page.emptyLabel).getTextValue(), is(""));
    }

    @Test
    public void should_return_the_actual_text_when_a_tag_has_any_text() {
        assertThat(page.element(page.nonEmptyLabel).getTextValue(), is("This div tag has text"));
    }

    @Test
    public void should_let_you_remove_the_focus_from_the_current_active_field() {
        StaticSitePage page = new StaticSitePage(chromeDriver, 1);
        page.open();

        page.element(page.firstName).click();

        assertThat(page.element(page.focusmessage).getText(), is(""));
        page.blurActiveElement();

        page.element(page.focusmessage).shouldContainText("focus left firstname");

    }

    @Test
    public void should_let_you_remove_the_focus_from_the_current_active_field_in_firefox() {

        if (runningOnLinux()) {
            StaticSitePage page = new StaticSitePage(driver, 1);

            page.open();

            page.element(page.firstName).click();

            assertThat(page.element(page.focusmessage).getText(), is(""));
            page.blurActiveElement();

            page.element(page.focusmessage).shouldContainText("focus left firstname");
        }
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
