package net.thucydides.junit.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import net.thucydides.core.annotations.At;
import net.thucydides.core.pages.PageObject;

@At("http://projects.apache.org")
public class ApacheProjectPage extends PageObject {

    public ApacheProjectPage(WebDriver driver) {
        super(driver);
    }
    
    public void clickOnProjects() {
        getDriver().findElement(By.linkText("Projects")).click();
    }

}
