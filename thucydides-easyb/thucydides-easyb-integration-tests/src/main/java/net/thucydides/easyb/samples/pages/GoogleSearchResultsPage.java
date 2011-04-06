package net.thucydides.easyb.samples.pages;

import java.util.ArrayList;
import java.util.List;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

//@DefaultUrl("/search")
public class GoogleSearchResultsPage extends PageObject {
    
    @FindBy(name="q")
    WebElement searchQuery;
    
    @FindBy(name="btnG")
    WebElement searchButton;
    
    public GoogleSearchResultsPage(WebDriver driver) {
        super(driver);
    }
 
    public List<String> getTopicTitles() {

        waitForRenderedElements(By.id("pnnext"));
        waitABit(500);

        List<String> titles = new ArrayList<String>();
        
        List<WebElement> titleElements = getDriver().findElements(By.cssSelector(".r"));
        for (WebElement titleElement : titleElements) {
            titles.add(titleElement.getText());
        }
        
        return titles;
    }


}