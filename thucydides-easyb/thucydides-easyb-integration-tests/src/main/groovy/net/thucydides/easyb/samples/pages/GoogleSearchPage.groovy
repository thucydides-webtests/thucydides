package net.thucydides.easyb.samples.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.By;

@DefaultUrl("http://www.google.com")
class GoogleSearchPage extends PageObject {
    
    @FindBy(name="q")
    WebElement searchQuery
    
    @FindBy(name="btnG")
    WebElement searchButton
    
    public GoogleSearchPage(WebDriver driver) {
        super(driver);
    }
    
    def searchFor(String query) {
        searchQuery.clear()
        searchQuery.sendKeys(query)
        clickOn(searchButton)
        waitForRenderedElements(By.id("pnnext"))
        waitABit 500
    }

}