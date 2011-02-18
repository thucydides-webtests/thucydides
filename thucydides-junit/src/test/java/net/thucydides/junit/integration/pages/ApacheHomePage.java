package net.thucydides.junit.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import net.thucydides.core.annotations.CompatibleWith;
import net.thucydides.core.pages.PageObject;

@CompatibleWith("http://www.apache.org")
public class ApacheHomePage extends PageObject {

    public ApacheHomePage(WebDriver driver) {
        super(driver);
    }
    
    public void clickOnProjects() {
        getDriver().findElement(By.linkText("Projects")).click();
    }

}
