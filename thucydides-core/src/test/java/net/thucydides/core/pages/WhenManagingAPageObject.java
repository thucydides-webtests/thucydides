package net.thucydides.core.pages;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.ElementNotDisplayedException;
import org.openqa.selenium.support.ui.Select;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WhenManagingAPageObject {

    @Mock
    WebDriver driver;

    @Mock
    Select mockSelect;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    final class BasicPageObject extends PageObject {
        public BasicPageObject(WebDriver driver) {
            super(driver);
        }
        
        @Override
        protected Select findSelectFor(WebElement dropdownList) {
            return mockSelect;
        }
    }

    @Test
    public void the_page_gets_the_title_from_the_web_page() {

        when(driver.getTitle()).thenReturn("Google Search Page");
        BasicPageObject page = new BasicPageObject(driver);
        
        assertThat(page.getTitle(), is("Google Search Page"));
    }
    
    @Test
    public void page_will_wait_for_rendered_element_if_it_is_already_present() {
        
        RenderedWebElement renderedElement = mock(RenderedWebElement.class);
        when(driver.findElement(any(By.class))).thenReturn(renderedElement);
        when(renderedElement.isDisplayed()).thenReturn(true);
        
        BasicPageObject page = new BasicPageObject(driver);
        page.waitForRenderedElements(By.id("whatever"));
    }
    
    @Test
    public void page_will_wait_for_rendered_element_if_it_is_not_already_present() {
        
        RenderedWebElement renderedElement = mock(RenderedWebElement.class);
        when(driver.findElement(any(By.class))).thenReturn(renderedElement);
        when(renderedElement.isDisplayed()).thenReturn(false).thenReturn(false).thenReturn(true);
        
        BasicPageObject page = new BasicPageObject(driver);
        page.waitForRenderedElements(By.id("whatever"));
    }
    
    @Test
    public void entering_a_value_in_a_field_will_clear_it_first() {
        WebElement field = mock(WebElement.class);
        BasicPageObject page = new BasicPageObject(driver);

        page.typeInto(field, "some value");
        
        verify(field).clear();
        verify(field).sendKeys("some value");
    }
    
    @Test
    public void picking_a_value_in_a_dropdown_picks_by_visible_text() {
        WebElement field = mock(WebElement.class);
        BasicPageObject page = new BasicPageObject(driver);

        page.selectFromDropdown(field, "Visible label");
        
        verify(mockSelect).selectByVisibleText("Visible label");
    }

    @Test(expected=NoSuchElementException.class)
    public void page_will_throw_exception_if_waiting_for_rendered_element_does_not_exist() {
        
        when(driver.findElement(any(By.class))).thenThrow(new NoSuchElementException("No such element"));
        
        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(100);
        page.waitForRenderedElements(By.id("whatever"));
    }    
    
    @Test(expected=ElementNotDisplayedException.class)
    public void page_will_throw_exception_if_waiting_for_rendered_element_is_not_visible() {
        
        RenderedWebElement renderedElement = mock(RenderedWebElement.class);
        when(driver.findElement(any(By.class))).thenReturn(renderedElement);
        when(renderedElement.isDisplayed()).thenReturn(false);
        
        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(100);
        page.waitForRenderedElements(By.id("whatever"));
    }     
    
    @Test
    public void page_object_should_know_when_a_field_is_visible() {
        BasicPageObject page = new BasicPageObject(driver);
        
        RenderedWebElement field = mock(RenderedWebElement.class);
        
        page.userCanSee(field);
        
        verify(field).isDisplayed();
        
    }

}
