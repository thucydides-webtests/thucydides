package net.thucydides.core.webdriver.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.util.Set;

import net.thucydides.core.pages.PageObject;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class WhenBrowsingAWebSiteUsingPageObjects {

    public class IndexPage extends PageObject {

        public WebElement multiselect;

        public IndexPage(WebDriver driver) {
            super(driver);
        }


    }
    
    WebDriver driver;
    
    @Before
    public void open_local_static_site() {
        driver = new HtmlUnitDriver();
        File baseDir = new File(System.getProperty("user.dir"));
        File testSite = new File(baseDir,"src/test/resources/static-site/index.html");
        driver.get("file://" + testSite.getAbsolutePath());
    }
    
    @Test
    public void should_find_page_title() {
        IndexPage indexPage = new IndexPage(driver);
        assertThat(indexPage.getTitle(), is("Thucydides Test Site"));
    } 

    @Test
    public void should_find_text_contained_in_page() {
        IndexPage indexPage = new IndexPage(driver);
        indexPage.shouldContainText("Some test pages");
    }

    @Test(expected=NoSuchElementException.class)
    public void should_not_find_text_not_contained_in_page() {
        IndexPage indexPage = new IndexPage(driver);
        indexPage.shouldContainText("This text is not in the pages");
    }

    @Test
    public void should_select_in_multiple_select_lists_correctly() {
        IndexPage indexPage = new IndexPage(driver);
        indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect,"Label 1", "Label 3");
        
        Set<String> selectedLabels = indexPage.getSelectedOptionLabelsFrom(indexPage.multiselect);
        assertThat(selectedLabels.size(), is(2));
        assertThat(selectedLabels, hasItems("Label 1", "Label 3"));
    }
    
    @Test
    public void should_select_values_in_multiple_select_lists_correctly() {
        IndexPage indexPage = new IndexPage(driver);
        indexPage.selectMultipleItemsFromDropdown(indexPage.multiselect,"Label 1", "Label 3");
        
        Set<String> selectedValues = indexPage.getSelectedOptionValuesFrom(indexPage.multiselect);
        assertThat(selectedValues.size(), is(2));
        assertThat(selectedValues, hasItems("1", "3"));
    }
    
}
