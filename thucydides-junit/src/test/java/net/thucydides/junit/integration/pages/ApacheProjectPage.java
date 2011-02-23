package net.thucydides.junit.integration.pages;

import net.thucydides.core.annotations.At;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@At("http://projects.apache.org")
public class ApacheProjectPage extends PageObject {

    @FindBy(linkText="Categories")
    WebElement categoriesLink;
    
    public ApacheProjectPage(WebDriver driver) {
        super(driver);
    }
    
    public void clickOnProjects() {
        getDriver().findElement(By.linkText("Projects")).click();
    }

    public void clickOnCategories() {
        categoriesLink.click();
    }

}
