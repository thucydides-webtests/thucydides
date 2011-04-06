package net.thucydides.gwt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.thucydides.gwt.pages.GwtShowcaseButtonPage;
import net.thucydides.gwt.widgets.GwtButton;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.ElementNotDisplayedException;

public class WhenWorkingWithGwtButtons {

    public static WebDriver driver;

    private GwtShowcaseButtonPage buttonPage;

    @BeforeClass
    public static void openBrowser() {
        driver = new FirefoxDriver();
        driver.get("http://gwt.google.com/samples/Showcase/Showcase.html#!CwBasicButton");
    }
    
    @Before
    public void setupPageObject() {
        buttonPage = new GwtShowcaseButtonPage(driver);
        buttonPage.waitForRenderedElements(By.xpath("//button"));
    }
    
    @Test
    public void a_normal_button_should_be_considered_enabled() {
        
        GwtButton normalButton = buttonPage.getButtonLabelled("Normal Button");
        assertThat(normalButton.isEnabled(), is(true));
        assertThat(normalButton.isDisabled(), is(false));
    }
    
    @Test
    public void a_disabled_button_should_be_considered_disabled() {
        
        GwtButton normalButton = buttonPage.getButtonLabelled("Disabled Button");
        assertThat(normalButton.isDisabled(), is(true));
        assertThat(normalButton.isEnabled(), is(false));
    }

    @Test
    public void a_normal_button_should_be_considered_visible() {
        GwtButton normalButton = buttonPage.getButtonLabelled("Normal Button");
        assertThat(normalButton.isVisible(), is(true));
    }
    

    @Test
    public void a_disabled_button_should_be_considered_visible() {
        GwtButton disabledButton = buttonPage.getButtonLabelled("Disabled Button");
        assertThat(disabledButton.isVisible(), is(true));
    }

    
    @Test
    public void a_test_can_wait_for_a_button_to_become_enabled() {
        RenderedWebElement mockButton = mock(RenderedWebElement.class);
        GwtButton disabledButton = new GwtButton("My button", mockButton);        
        when(mockButton.isEnabled()).thenReturn(false, false, true);
        
        disabledButton.waitUntilEnabled();
    }

    @Test(expected=ElementNotDisplayedException.class) 
    public void a_test_should_fail_if_the_button_doesnt_become_enabled_after_a_certain_time() {
        RenderedWebElement mockButton = mock(RenderedWebElement.class);
        GwtButton disabledButton = new GwtButton("My button", mockButton);
        disabledButton.setWaitForTimeout(100);        
        when(mockButton.isEnabled()).thenReturn(false);
        
        disabledButton.waitUntilEnabled();
    }

    @Test
    public void you_can_also_find_a_gwt_button_using_a_By_expression() {
        GwtButton normalButton = buttonPage.findButton(By.id("gwt-debug-cwBasicButton-normal"));
        assertThat(normalButton.isEnabled(), is(true));
    }


    @Test
    public void a_button_identified_by_a_By_expression_should_still_return_the_correct_label() {
        GwtButton normalButton = buttonPage.findButton(By.id("gwt-debug-cwBasicButton-normal"));
        assertThat(normalButton.getLabel(), is("Normal Button"));
    }

    @Test
    public void a_button_can_be_instanciated_directly_from_a_web_element() {
        GwtButton normalButton = new GwtButton(driver.findElement(By.id("gwt-debug-cwBasicButton-normal")));
        assertThat(normalButton.getLabel(), is("Normal Button"));
    }


    @AfterClass
    public static void closeBrowser() {
        driver.quit();
    }
}
