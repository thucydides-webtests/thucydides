package net.thucydides.core.pages;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class WhenManagingAPageObject {

    @Mock
    WebDriver driver;

    @Mock
    Select mockSelect;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
    
    class BasicPageObject extends PageObject {
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
        List<WebElement> renderedElements = new ArrayList<WebElement>();
        renderedElements.add(renderedElement);
        
        when(driver.findElement(any(By.class))).thenReturn(renderedElement);
        when(driver.findElements(any(By.class))).thenReturn(renderedElements);
        
        when(renderedElement.isDisplayed()).thenReturn(true);
        
        BasicPageObject page = new BasicPageObject(driver);
        page.waitForRenderedElements(By.id("whatever"));
    }
    
    @Test
    public void page_will_wait_for_rendered_element_to_disappear() {
        
        List<WebElement> emptyList = Arrays.asList();
        when(driver.findElements(any(By.class))).thenReturn(emptyList);
        
        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(100);
        page.waitForRenderedElementsToDisappear(By.id("whatever"));
    }

    @Test
    public void page_will_wait_for_rendered_element_if_it_is_not_already_present() {
        
        RenderedWebElement renderedElement = mock(RenderedWebElement.class);
        List<WebElement> renderedElements = new ArrayList<WebElement>();
        renderedElements.add(renderedElement);
        
        when(driver.findElement(any(By.class))).thenReturn(renderedElement);
        when(driver.findElements(any(By.class))).thenReturn(renderedElements);
        when(renderedElement.isDisplayed()).thenReturn(false).thenReturn(false).thenReturn(true);
        
        BasicPageObject page = new BasicPageObject(driver);
        page.setWaitForTimeout(100);
        page.waitForRenderedElements(By.id("whatever"));
    }
    
    @Test
    public void page_will_wait_for_text_to_appear_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement textBlock = mock(WebElement.class);
        
        List<WebElement> emptyList = Arrays.asList();
        List<WebElement> listWithElements = Arrays.asList(textBlock);
        
        when(driver.findElements(any(By.class))).thenReturn(emptyList).thenReturn(listWithElements);

        page.waitForTextToAppear("hi there");
    }

    @Test
    public void page_will_wait_for_text_to_appear_in_element_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement textBlock = mock(WebElement.class);
        WebElement searchedBlock = mock(WebElement.class);
        
        List<WebElement> emptyList = Arrays.asList();
        List<WebElement> listWithElements = Arrays.asList(textBlock);
        
        when(searchedBlock.findElements(any(By.class))).thenReturn(emptyList).thenReturn(listWithElements);

        page.waitForTextToAppear(searchedBlock,"hi there");
    }

    @Test
    public void page_will_wait_for_text_to_appear_in_an_element_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement textBlock = mock(WebElement.class);
        WebElement searchedBlock = mock(WebElement.class);
        
        List<WebElement> emptyList = Arrays.asList();
        List<WebElement> listWithElements = Arrays.asList(textBlock);
        
        when(searchedBlock.findElements(any(By.class))).thenReturn(emptyList).thenReturn(listWithElements);

        page.waitForAnyTextToAppear(searchedBlock, "hi there");
    }

    @Test(expected=ElementNotDisplayedException.class)
    public void page_will_fail_if_single_text_fails_to_appear_in_an_element_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement searchedBlock = mock(WebElement.class);
        
        List<WebElement> emptyList = Arrays.asList();
        
        when(searchedBlock.findElements(any(By.class))).thenReturn(emptyList);
        page.setWaitForTimeout(100);
        page.waitForAnyTextToAppear(searchedBlock, "hi there");
    }
    
    @Test(expected=ElementNotDisplayedException.class)
    public void page_will_fail_if_text_fails_to_appear_in_an_element_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement searchedBlock = mock(WebElement.class);
        
        List<WebElement> emptyList = Arrays.asList();
        
        when(searchedBlock.findElements(any(By.class))).thenReturn(emptyList);

        page.setWaitForTimeout(100);
        page.waitForAnyTextToAppear(searchedBlock, "hi there");
    }
    
    @Test
    public void page_will_wait_for_text_to_disappear_if_requested() {

        BasicPageObject page = new BasicPageObject(driver);
        WebElement textBlock = mock(WebElement.class);
        
        List<WebElement> emptyList = Arrays.asList();
        List<WebElement> listWithElements = Arrays.asList(textBlock);
        
        when(driver.findElements(any(By.class))).thenReturn(listWithElements).thenReturn(emptyList);

        page.waitForTextToDisappear("hi there");
    }
    
    @Test(expected=NoSuchElementException.class)
    public void should_contain_text_should_throw_an_assertion_if_text_is_not_visible() {
        BasicPageObject page = new BasicPageObject(driver);        
        List<WebElement> emptyList = Arrays.asList();        
        when(driver.findElements(any(By.class))).thenReturn(emptyList);
        
        page.shouldContainText("hi there");
    }
    
    @Test
    public void should_contain_text_should_do_nothing_if_text_is_present() {
        WebElement textBlock = mock(WebElement.class);
        BasicPageObject page = new BasicPageObject(driver);        
        List<WebElement> emptyList = Arrays.asList(textBlock);        
        when(driver.findElements(any(By.class))).thenReturn(emptyList);
        
        page.shouldContainText("hi there");
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
    
    @Test(expected=AssertionError.class)
    public void should_be_visible_should_throw_an_assertion_if_element_is_not_visible() {
        BasicPageObject page = new BasicPageObject(driver);       
        RenderedWebElement field = mock(RenderedWebElement.class);
        when(field.isDisplayed()).thenReturn(false);
        
        page.shouldBeVisible(field);
    }
    
    @Test
    public void should_be_visible_should_do_nothing_if_element_is_visible() {
        BasicPageObject page = new BasicPageObject(driver);       
        RenderedWebElement field = mock(RenderedWebElement.class);
        when(field.isDisplayed()).thenReturn(true);
        
        page.shouldBeVisible(field);
    }

    @Test
    public void the_page_should_initially_open_at_the_systemwide_default_url() {

        System.setProperty("webdriver.base.url","http://www.google.com");

        BasicPageObject page = new BasicPageObject(driver);

        Pages pages = new Pages(driver);
        pages.start();

        verify(driver).get("http://www.google.com");
        System.setProperty("webdriver.base.url","");
    }

    @Test
    public void the_start_url_for_a_set_of_pages_can_be_overridden() {
        BasicPageObject page = new BasicPageObject(driver);
        PageConfiguration.getCurrentConfiguration().setDefaultBaseUrl("http://www.google.com");

        Pages pages = new Pages(driver);
        pages.setDefaultBaseUrl("http://www.google.co.nz");
        pages.start();

        verify(driver).get("http://www.google.co.nz");
    }

}
