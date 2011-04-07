package net.thucydides.easyb.samples.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@DefaultUrl("http://www.google.com")
class GoogleSearchSteps extends PageObject {

    @FindBy(name="q")
    WebElement searchQuery

    @FindBy(name="btnG")
    WebElement searchButton

    public GoogleSearchSteps(WebDriver driver) {
        super(driver);
    }
    
    def searchFor(String query) {
        searchQuery.clear()
        searchQuery.sendKeys(query)
        searchButton.click()
    }
}