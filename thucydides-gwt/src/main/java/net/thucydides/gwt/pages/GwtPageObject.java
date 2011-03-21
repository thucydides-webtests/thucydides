package net.thucydides.gwt.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import net.thucydides.core.pages.PageObject;

/**
 * An extension of the Thucydides PageObject class proviging extra features for GWT apps
 * 
 * @author johnsmart
 * 
 */
public abstract class GwtPageObject extends PageObject {

    public GwtPageObject(WebDriver driver) {
        super(driver);
    }
    
    public boolean buttonIsDisabled(String label) {
        boolean disabled = false;
        getDriver().findElements(By.cssSelector(".gwt-Button[disabled]"));
        
        return disabled;
    }

}
