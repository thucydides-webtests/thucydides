package net.thucydides.core.pages.integration;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.By;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenWaitingForElementsWithTheFluentElementAPI extends FluentElementAPITestsBaseClass {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_obtain_text_value_from_input() {
        StaticSitePage page = getFirefoxPage();
        assertThat(page.element(page.firstName).getValue(), is("<enter first name>"));
    }

    @Test
    public void should_optionally_type_enter_after_entering_text() {
        StaticSitePage page = getChromePage();
        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndEnter("joe");

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));
    }

    @Test
    public void should_optionally_type_tab_after_entering_text_on_linux() {

        if (runningOnLinux()) {
            StaticSitePage page = getChromePage();

            assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

            page.element(page.firstName).typeAndTab("joe");

            assertThat(page.element(page.lastName).hasFocus(), is(true));
        }
    }

    @Test
    public void should_optionally_type_tab_after_entering_text_in_firefox() {

        StaticSitePage page = getFirefoxPage();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndTab("joe");
        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_trigger_blur_event_when_focus_leaves_field_in_chrome() {

        StaticSitePage page = getChromePage();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        assertThat(page.focusmessage.getText(), is(""));

        page.element(page.firstName).typeAndTab("joe");

        assertThat(page.focusmessage.getText(), is("focus left firstname"));
    }

    @Test
    public void should_wait_for_element_to_be_visible_and_enabled_before_clicking() {
        StaticSitePage page = getFirefoxPage();
        page.element(page.checkbox).click();

    }


    @Test
    public void should_be_able_to_build_composite_wait_until_enabled_clauses() throws InterruptedException {
        StaticSitePage page = getFirefoxPage();

        page.waitForCondition().until(page.firstAndLastNameAreEnabled());
    }

    @Test
    public void should_be_able_to_build_composite_wait_until_disabled_clauses() throws InterruptedException {
        StaticSitePage page = getFirefoxPage();

        page.waitForCondition().until(page.twoFieldsAreDisabled());
    }


    @Test
    public void should_let_you_remove_the_focus_from_the_current_active_field() {
        StaticSitePage page = getChromePage();

        page.element(page.firstName).click();

        assertThat(page.element(page.focusmessage).getText(), is(""));
        page.blurActiveElement();

        page.element(page.focusmessage).shouldContainText("focus left firstname");

    }

    @Test
    public void should_let_you_remove_the_focus_from_the_current_active_field_in_firefox() {

      if (runningOnLinux()) {
           StaticSitePage page = getFirefoxPage();
            page.element(page.firstName).click();

            assertThat(page.element(page.focusmessage).getText(), is(""));
            page.blurActiveElement();

            page.element(page.focusmessage).shouldContainText("focus left firstname");
      }
    }


    @Test
    public void should_wait_for_text_to_dissapear() {
        StaticSitePage page = getFirefoxPage();
        page.waitForTextToDisappear("Dissapearing text");

        assertThat(page.containsText("Dissapearing text"), is(false));
    }

    @Test
    public void should_wait_for_text_in_element_to_dissapear() {
        StaticSitePage page = getFirefoxPage();
        page.waitForTextToDisappear(page.dissapearingtext, "Dissapearing text");

        assertThat(page.containsText("Dissapearing text"), is(false));
    }

    @Test
    public void should_wait_for_elements_to_appear() {
        StaticSitePage page = getChromePage();
        page.waitForAnyRenderedElementOf(By.id("city"));
        assertThat(page.element(page.city).isCurrentlyVisible(), is(true));
    }

    @Test
    public void should_wait_for_field_to_be_enabled_using_alternative_style() throws InterruptedException {
        StaticSitePage page = getFirefoxPage();

        page.firstName().waitUntilVisible();
        page.firstName().waitUntilEnabled();
    }

}
