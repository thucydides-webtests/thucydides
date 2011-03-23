package net.thucydides.gwt.pages;

import net.thucydides.gwt.widgets.GwtButton;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebElement;

/**
 * An extension of the Thucydides PageObject class providing extra features for GWT apps.
 * 
 * @author johnsmart
 * 
 */
public abstract class GwtPageObject extends PageObject {

    public GwtPageObject(WebDriver driver) {
        super(driver);
    }

    /**
     * Return a GWT Button widget with a given label.
     */
    public GwtButton getButtonLabelled(String label) {
        String buttonXPath = String.format("//button[contains(.,'%s')]", label);
        WebElement button = getDriver().findElement(By.xpath(buttonXPath));
        return new GwtButton(label, button);
    }

}
