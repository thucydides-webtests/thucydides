package net.thucydides.core.pages.integration;


import net.thucydides.core.webdriver.StaticTestSite;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class WhenWaitingForElementsWithTheFluentElementAPI extends FluentElementAPITestsBaseClass {

    StaticSitePage page;
    StaticSitePage chromePage;

    private StaticTestSite testSite;

    @BeforeClass
    public static void setup() {
        System.setProperty("saucelabs.url","http://thucydides:98e053c0-ebdf-4906-a68c-1bf6049aa41f@ondemand.saucelabs.com:80/wd/hub");
    }
    @Before
    public  void setupDriver() {

        testSite = new StaticTestSite();

        driver = testSite.open();
        chromeDriver = testSite.open("chrome");

        page = new StaticSitePage(driver, 1000);
        page.open();
        page.addJQuerySupport();

        chromePage = new StaticSitePage(chromeDriver, 1000);
        chromePage.open();
    }

    @After
    public  void closeBrowser() {
        driver.quit();
        chromeDriver.quit();
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_obtain_text_value_from_input() {
        ////refresh(page);
        assertThat(page.element(page.firstName).getValue(), is("<enter first name>"));
    }

    @Test
    public void should_optionally_type_enter_after_entering_text() {
        //refresh(chromePage);
        assertThat(chromePage.firstName.getAttribute("value"), is("<enter first name>"));

        chromePage.element(chromePage.firstName).typeAndEnter("joe");

        assertThat(chromePage.firstName.getAttribute("value"), is("<enter first name>"));
    }

    @Test
    public void should_optionally_type_tab_after_entering_text_on_linux() {

        if (runningOnLinux()) {
            //refresh(chromePage);

            assertThat(chromePage.firstName.getAttribute("value"), is("<enter first name>"));

            chromePage.element(chromePage.firstName).typeAndTab("joe");

            assertThat(chromePage.element(chromePage.lastName).hasFocus(), is(true));
        }
    }

    @Test
    public void should_optionally_type_tab_after_entering_text_in_firefox() {

        ////refresh(page);

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        page.element(page.firstName).typeAndTab("joe");
        assertThat(page.element(page.lastName).hasFocus(), is(true));
    }

    @Test
    public void should_trigger_blur_event_when_focus_leaves_field_in_chrome() {

        StaticSitePage page = new StaticSitePage(chromeDriver, 750);

        page.open();

        assertThat(page.firstName.getAttribute("value"), is("<enter first name>"));

        assertThat(page.focusmessage.getText(), is(""));

        page.element(page.firstName).typeAndTab("joe");

        assertThat(page.focusmessage.getText(), is("focus left firstname"));
    }

    @Test
    public void should_wait_for_element_to_be_visible_and_enabled_before_clicking() {
        page.element(page.checkbox).click();

    }


    @Test
    public void should_be_able_to_build_composite_wait_until_enabled_clauses() throws InterruptedException {
        ////refresh(page);

        page.waitForCondition().until(page.firstAndLastNameAreEnabled());
    }

    @Test
    public void should_be_able_to_build_composite_wait_until_disabled_clauses() throws InterruptedException {
        ////refresh(page);

        page.waitForCondition().until(page.twoFieldsAreDisabled());
    }


    @Test
    public void should_let_you_remove_the_focus_from_the_current_active_field() {
        StaticSitePage page = new StaticSitePage(chromeDriver, 750);
        ////refresh(page);

        page.element(page.firstName).click();

        assertThat(page.element(page.focusmessage).getText(), is(""));
        page.blurActiveElement();

        page.element(page.focusmessage).shouldContainText("focus left firstname");

    }

    @Test
    public void should_let_you_remove_the_focus_from_the_current_active_field_in_firefox() {

      if (runningOnLinux()) {
            page.element(page.firstName).click();

            assertThat(page.element(page.focusmessage).getText(), is(""));
            page.blurActiveElement();

            page.element(page.focusmessage).shouldContainText("focus left firstname");
      }
    }


    @Test
    public void should_wait_for_text_to_dissapear() {
        ////refresh(page);
        page.waitForTextToDisappear("Dissapearing text");

        assertThat(page.containsText("Dissapearing text"), is(false));
    }

    @Test
    public void should_wait_for_text_in_element_to_dissapear() {
        ////refresh(page);
        page.waitForTextToDisappear(page.dissapearingtext, "Dissapearing text");

        assertThat(page.containsText("Dissapearing text"), is(false));
    }

    @Test
    public void should_wait_for_elements_to_appear() {
        //refresh(chromePage);
        chromePage.waitForAnyRenderedElementOf(By.id("city"));
        assertThat(chromePage.element(chromePage.city).isCurrentlyVisible(), is(true));
    }

    @Test
    public void should_display_meaningful_error_messages_in_firefox_if_waiting_for_field_that_does_not_appear() {
        boolean assertionThrown = false;
        String exceptionMessage = null;
        try {

            ////refresh(page);

            page.setWaitForTimeout(200);
            ////refresh(page);

            page.element(page.fieldDoesNotExist).waitUntilVisible();

        } catch(ElementNotVisibleException e) {
            assertionThrown = true;
            exceptionMessage = e.getMessage();
        }
        assertThat(assertionThrown, is(true));
        assertThat(exceptionMessage,
                allOf(containsString("Unable to locate element"), containsString("fieldDoesNotExist")));

    }


    @Test
    public void should_wait_for_field_to_be_enabled_using_alternative_style() throws InterruptedException {
        page.firstName().waitUntilVisible();
        page.firstName().waitUntilEnabled();
    }

}
